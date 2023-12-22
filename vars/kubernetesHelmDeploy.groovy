def call (String dockerRegistry, String dockerImageTag, String helmChartName) {
    sh """
        if ! command -v helm > /dev/null; then
            echo "Helm not found. Installing Helm..."
            sudo curl -fsSL -S -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
            sudo chmod 700 get_helm.sh
            sudo ./get_helm.sh 
            sudo rm -f get_helm.sh
            echo "Helm installed successfully."
        fi
    """
    
    sh  """
    helm upgrade --install $helmChartName ./helm/helm-deploy-sharedlibrary/ -n ingress-nginx --set image.repository="$dockerRegistry:$dockerImageTag" 
"""
}

