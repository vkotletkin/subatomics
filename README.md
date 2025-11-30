# Getting Started

Create in repo:

core/schemas/.gitkeep

apps/.gitkeep

Create applicationset:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: repo-apps
  namespace: argocd
  annotations:
    argocd.argoproj.io/finalizer-format: "domain-path"
spec:
  generators:
    - git:
        repoURL: http://192.168.207.128:80/vkotletkin/test-argo.git
        revision: main
        directories:
          - path: "apps/**"          # Включаем все директории и файлы
  template:
    metadata:
      name: '{{path.basename}}'
    spec:
      project: default
      source:
        repoURL: http://192.168.207.128:80/vkotletkin/test-argo.git
        targetRevision: main
        path: '{{path}}'
      destination:
        server: https://kubernetes.default.svc
      syncPolicy:
        automated:
          prune: true
        syncOptions:
          - CreateNamespace=true
          - ServerSideApply=true
      ignoreDifferences:
        - group: "*"
          kind: "*"
          jsonPointers:
            - /metadata/annotations/argocd.argoproj.io~1tracking-id
            - /metadata/managedFields
```

```shell
kubectl patch configmap argocd-cm -n argocd --type merge   -p '{"data":{"timeout.reconciliation":"30s","timeout.reconciliation.jitter":"0s"}}'
```

