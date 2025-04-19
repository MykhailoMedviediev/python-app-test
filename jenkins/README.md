# Jenkins Agent

Jenkins with `kubectl` command in it to manupulate Kubernetes cluster. 


## Set Up CI/CD

Open Jenkins UI in your browser and perform Jenkins Initial Setup. After this navigate to `Manage Jenkins` -> `Plugins` and install Docker and Kubernetes plugins.

### Retrieve Kubernetes server certificate key

**To retrieve Kubernetes server certificate key use the following command:**

```sh
kubectl get secret -n devops-tools $(kubectl get sa jenkins-sa -n devops-tools -o jsonpath="{.secrets[0].name}") -o jsonpath="{.data['ca\.crt']}"
```

### Create credentials for SA

For Kubernetes v1.24 and later, the kubeadm-managed default ServiceAccount token is no longer auto-created.
So, for Jenkins to authenticate with the Kubernetes API, you’ll need to manually create a token.

1. Copy token from output of `kubectl create token jenkins-sa -n devops-tools --duration=8760h` command.
2. Navigate to `Manage Jenkins` -> `Credentials`.
3. Create Credentials of kind `Secret text` to use it when configuring Cloud.

### Configure Kubernetes Cloud:

1. Navigate to `Manage Jenkins` -> `Clouds`.
2. Create new Cloud:
- Kubernetes URL: https://kubernetes.default.
- Kubernetes server certificate key: paste [retrieved key](#retrieve-kubernetes-server-certificate-key).
- Kubernetes Namespace: `devops-tools`.
- Credentials: use [SA token credentials](#create-credentials-for-sa).

### Create Pod Template:

- Namespace: `devops-tools`.
- Labels: `docker`.
- Containers:
    - Name: `jnlp`.
    - Docker image: `medviedievm/jenkins-worker-node-docker-kubectl:v0.1`.
    - Working directory: `/home/jenkins/agent`.
    - Arguments to pass to the command: `${JENKINS_URL}`.
    - Environment Variable:
        - Key: `JENKINS_URL`.
        - Value: `http://jenkins-internal.devops-tools.svc.cluster.local:8080`.
- Volumes:
    - Host Path Volume:
        Host path: `/var/run/docker.sock`.
        Mount path: `/var/run/docker.sock`.
- Service Account: `jenkins-agent`.
- Workspace Volume: `Empty Dir Workspace Volume`.

### Create credentials for GitHub:

- Kind: `SSH Username with private key`.
- Username: `git`.

### Create credentials for DockerHub:

- Kind: `username with password`.
- Password: token, generated from your Docker Hub account.

### Create Jenkins job:

1. Navigate to `Dashboard`.
2. Click `New Item`.
3. For **Pipeline Definition** choose `Pipeline Script from SCM`.
4. Click `Add Repository`, select git credentials, and paste repository's SSH URL.
5. Save job.

---

## Jenkinsfile Overview

### `GIT_COMMIT_HASH` environment variable

To ensures that each image version tag is uniquely tied to a specific git commit the following script creates `GIT_COMMIT_HASH` environment variable, which will be used with the build number for naming:
```
script {
    env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short=5 HEAD', returnStdout: true).trim()
}
```

### `withRegistry` method

In the `Push` stage, `withRegistry` method is used to provide Docker Hub credentials.
```
docker.withRegistry('', 'docker-hub-credentials') {   
    image.push()
}
```

### `deployToKubernetes` function

In the Deploy stage, `deployToKubernetes` function executes the following commands:

```sh
kubectl set image deployment/${appName}-deploy ${appName}=${imageWithTag} --record -n default # # Updates the image of the deployment's container to the new version and records the change in rollout history
kubectl rollout status deployment/${appName}-deploy -n default # Waits for the rollout to complete and verifies that the deployment was successful
```
