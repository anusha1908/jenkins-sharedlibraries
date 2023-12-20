def call (String dockerImageTag, String helmChartName) {
    sh """
        if ! command -v helm > /dev/null; then
            echo "Helm not found. Installing Helm..."
            curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
            chmod 700 get_helm.sh
            sudo ./get_helm.sh > /dev/null
            rm -f get_helm.sh
            echo "Helm installed successfully."
        fi
    """
    sh 'helm upgrade --install $helmChartName ./helm/helm-deploy-sharedlibrary/ --set deployment.image="$dockerRegistry:$dockerImageTag" '
    //sh "helm upgrade first --install $helmChartName ./helm/helm-deploy-sharedlibrary/ --set image.tag=$dockerImageTag"
}

