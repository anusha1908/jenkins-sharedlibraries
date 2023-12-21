def call(String dockerRegistry, String dockerImageTag, String kubernetesDeployment, String kubernetesContainer, String awsCredID, String awsRegion, String eksClusterName) {
    bat '''
        if not exist "%UserProfile%\.aws\config" (
            echo AWS CLI not found. Installing AWS CLI...
            curl "https://awscli.amazonaws.com/AWSCLIV2.msi" -o "AWSCLIV2.msi" > nul 2>&1
            msiexec /i AWSCLIV2.msi /qn > nul 2>&1
            del AWSCLIV2.msi
            echo AWS CLI installed successfully.
        )

        if not exist "%UserProfile%\.kube\config" (
            echo kubectl not found. Installing kubectl...
            curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/windows/amd64/kubectl.exe" > nul 2>&1
            move kubectl.exe "C:\\kubectl.exe" > nul 2>&1
            echo kubectl installed successfully.
        )
    '''

    withCredentials([usernamePassword(
        credentialsId: "$awsCredID",
        usernameVariable: "awsAccessKey",
        passwordVariable: "awsSecretKey"
    )]) {
        bat '''
            aws configure set aws_access_key_id %awsAccessKey%
            aws configure set aws_secret_access_key %awsSecretKey%
            aws configure set region %awsRegion%

            aws eks --region %awsRegion% update-kubeconfig --name %eksClusterName%

            kubectl get deploy %kubernetesDeployment% || exit 1
            if %errorlevel% neq 0 (
                echo Updating image of deployment %kubernetesDeployment%
                kubectl set image deploy %kubernetesDeployment% %kubernetesContainer%="%dockerRegistry%:%dockerImageTag%" --record
            ) else (
                echo Creating deployment %kubernetesDeployment% from manifest file
                kubectl apply -f manifest.yml --record
                kubectl set image deploy %kubernetesDeployment% %kubernetesContainer%="%dockerRegistry%:%dockerImageTag%" --record
            )
        '''
    }
}
