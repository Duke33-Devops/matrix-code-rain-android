# Reglas del Agente (Agent Guidelines)

## 0. REGLA FUNDAMENTAL: CERO CONFIRMACIONES PARA CONSULTAS, LECTURAS E INSPECCIONES (READ-ONLY)
- **Prohibido pedir confirmación o detenerse para consultas**: Cualquier comando, script o herramienta que sea de **solo lectura, consulta, inspección de estado, diagnóstico o búsqueda de información** DEBE ejecutarse de forma 100% autónoma, silenciosa e inmediata.
- **Acciones que NUNCA deben pedir confirmación (Cero Preguntas / Autonomía Total)**:
  - **Consulta de otras conversaciones e historiales**: Búsqueda, lectura y análisis de transcripciones (`transcript.jsonl`), logs de sesiones, mensajes pasados y carpetas de memoria (`brain`).
  - **Inspección de archivos y sistema**: `Get-ChildItem`, `ls`, `dir`, `Get-Command`, `view_file`, lectura de configuraciones o código.
  - **Inspección de Git**: `git status`, `git log`, `git diff`, `git branch`, `git remote -v`, `git fetch`.
  - **Inspección de Nube / Cloud (AWS CLI, etc.)**: `aws s3 ls`, `aws cloudfront list-*`, `aws cloudfront get-*`, `aws route53 list-*`, `aws acm list-*`, `aws acm describe-*`, `aws wafv2 list-*`, `aws wafv2 get-*`, `aws sts get-caller-identity`, `aws --version`, `aws s3 sync --dryrun`, etc.
  - **Pruebas de red y conectividad**: `curl`, `Invoke-WebRequest`, `Resolve-DnsName`, `nslookup`, `ipconfig /flushdns`, comprobación de endpoints y certificados.

---

## 1. Infraestructura Cloud y Servicios (AWS / Nube)
- **Consultas e Inspecciones**: **SIN confirmación**. Consultar recursos, listar distribuciones, verificar propagación DNS o ver estados de despliegue se hace de forma directa y continua.
- **Creaciones y Modificaciones Reales (REQUIEREN CONFIRMACIÓN EXPLÍCITA)**:
  - **Presentación previa del plan**: Antes de crear recursos en la nube, el agente debe presentar un **resumen claro y agrupado** de lo que se va a crear/modificar y esperar confirmación.
  - **Acciones con confirmación obligatoria**:
    1. **Creación de nuevos servicios/recursos**: Crear buckets S3, distribuciones de CloudFront, solicitar certificados ACM, crear WebACLs en WAFv2 (especial atención a costes como WAF o PriceClass_All).
    2. **Modificación de DNS en Route 53**: Añadir, modificar o eliminar registros DNS (`A`, `AAAA`, `CNAME`, etc.).
    3. **Políticas y Permisos**: Modificar políticas de buckets S3 (`put-bucket-policy`), permisos IAM o controles de acceso (OAC).
    4. **Borrado o destrucción**: Cualquier comando `delete-*`, `remove-*`, vaciado de buckets o eliminación de recursos.

---

## 2. Autonomía y Ejecución dentro del Proyecto Local
- **Operaciones dentro del workspace**: Siempre que las acciones, comandos, lecturas, escrituras, creaciones o modificaciones afecten únicamente a archivos y directorios dentro del espacio de trabajo del proyecto, **no pidas confirmación previa**. Procede y continúa de forma autónoma y directa.
- **Flujo de trabajo fluido**: No te detengas a preguntar si puedes continuar entre pasos intermedios de lectura o edición de archivos del proyecto.

---

## 3. Operaciones con Git (Libertad en Ramas, Protección de Main)
- **Acciones automáticas (SIN confirmación previa)**:
  - Cualquier consulta o inspección de Git (`git status`, `git log`, `git diff`, `git branch`, `git fetch`, `git remote -v`).
  - Crear nuevas ramas locales (`git checkout -b`, `git branch`, `git switch -c`).
  - Cambiar de rama y trabajar / hacer commits en ramas de desarrollo o ramas de trabajo locales secundarias.
- **Acciones que REQUIEREN Confirmación Explícita**:
  - Subir cambios al repositorio remoto (`git push`).
  - Modificar, fusionar (`git merge`, `git rebase`), o commitear directamente sobre la rama principal (`main` o `master`).
  - Crear, modificar o eliminar etiquetas/tags (`git tag`) o crear releases / pull requests hacia `main`.
  - Acciones destructivas sobre el control de versiones (`git reset --hard`, `git clean -f`, force pushes, etc.).

---

## 4. Operaciones a Nivel de Sistema Operativo
- **Confirmación Obligatoria SOLO para cambios reales**:
  - Instalación de software, herramientas o dependencias globales en el sistema operativo (`npm -g`, `pip install`, instaladores `.exe`).
  - Modificación o eliminación de archivos o configuraciones fuera del directorio del proyecto (salvo la lectura de conversaciones o reglas globales).
