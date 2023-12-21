def call (String dockerRegistry, String dockerImageTag, String kubernetesDeployment, String kubernetesContainer, String awsCredID, String awsRegion, String eksClusterName) {
   
    withCredentials([usernamePassword(
        credentialsId: "$awsCredID",
        usernameVariable: "awsAccessKey",
        passwordVariable: "awsSecretKey"
    )]) {
            sh """
                aws configure set aws_access_key_id $awsAccessKey
                aws configure set aws_secret_access_key $awsSecretKey
                aws configure set region $awsRegion

                aws eks --region $awsRegion update-kubeconfig --name $eksClusterName

                kubectl get deploy $kubernetesDeployment || true; if [ \$? -ne 0 ]; then
                    echo "Updating image of deployment $kubernetesDeployment"
                    kubectl set image deploy $kubernetesDeployment -n ingress-nginx $kubernetesContainer="$dockerRegistry:$dockerImageTag" --record
                else
                    echo "Creating deployment $kubernetesDeployment from manifest file"
                    kubectl apply -f deploymentserviceCluster.yaml -n ingress-nginx --record
                    kubectl set image deploy $kubernetesDeployment -n ingress-nginx $kubernetesContainer="$dockerRegistry:$dockerImageTag" --record
                fi
            """
        }
}
