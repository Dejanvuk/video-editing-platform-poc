set -ex

kubectl apply -f .

kubectl wait --timeout=500s --for=condition=ready pod --all

set +ex
