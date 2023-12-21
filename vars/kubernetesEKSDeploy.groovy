def call (String dockerRegistry, String dockerImageTag, String kubernetesDeployment, String kubernetesContainer, String awsCredentialId, String awsRegion, String eksClusterName) {
   

                aws eks --region $awsRegion update-kubeconfig --name $eksClusterName

                kubectl get deploy $kubernetesDeployment || true; if [ \$? -ne 0 ]; then
                    echo "Updating image of deployment $kubernetesDeployment"
                    kubectl set image deploy $kubernetesDeployment $kubernetesContainer="$dockerRegistry:$dockerImageTag" --record
                else
                    echo "Creating deployment $kubernetesDeployment from manifest file"
                    kubectl apply -f manifest.yml --record
                    kubectl set image deploy $kubernetesDeployment $kubernetesContainer="$dockerRegistry:$dockerImageTag" --record
                fi
            
