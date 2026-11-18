/*
 * Reqestra UI locale catalog.
 * Source identifiers are historical Spanish UI labels, while English is the
 * default fallback. Never send request/response data to a translation service.
 */
export const LOCALES = Object.freeze([
  {code:"en",name:"English"},
  {code:"es",name:"Español"},
  {code:"ru",name:"Русский"},
  {code:"zh",name:"简体中文"},
  {code:"pt",name:"Português"},
  {code:"fr",name:"Français"},
]);
const rows = [
  // spanish identifier, english, russian, simplified chinese, portuguese, french
  ["Estudio","Studio","Студия","工作台","Estúdio","Studio"],
  ["Colecciones","Collections","Коллекции","集合","Coleções","Collections"],
  ["Pruebas","Tests","Тесты","测试","Testes","Tests"],
  ["Explorar","Explore","Исследовать","探索","Explorar","Explorer"],
  ["Historial","History","История","历史记录","Histórico","Historique"],
  ["Inspector","Inspector","Инспектор","检查器","Inspetor","Inspecteur"],
  ["Vista previa","Preview","Предпросмотр","预览","Pré-visualização","Aperçu"],
  ["Sesiones","Sessions","Сессии","会话","Sessões","Sessions"],
  ["Extensiones","Extensions","Расширения","扩展","Extensões","Extensions"],
  ["Donar / Ayuda","Donate / Help","Поддержать / Помощь","捐赠 / 帮助","Doar / Ajuda","Don / Aide"],
  ["Más","More","Ещё","更多","Mais","Plus"],
  ["Saltar al contenido","Skip to content","Перейти к содержимому","跳到内容","Ir para o conteúdo","Aller au contenu"],
  ["ESPACIO DE TRABAJO","WORKSPACE","РАБОЧАЯ ОБЛАСТЬ","工作区","ESPAÇO DE TRABALHO","ESPACE DE TRAVAIL"],
  ["Cliente local · sin telemetría","Local client · no telemetry","Локальный клиент · без телеметрии","本地客户端 · 无遥测","Cliente local · sem telemetria","Client local · aucune télémétrie"],
  ["Las peticiones HTTP se envían solo cuando tú lo ordenas.","HTTP requests are sent only when you choose.","HTTP-запросы отправляются только по вашей команде.","仅在您操作时发送 HTTP 请求。","Requisições HTTP são enviadas somente quando você decidir.","Les requêtes HTTP ne sont envoyées que sur votre demande."],
  ["Modo temporal","Temporary mode","Временный режим","临时模式","Modo temporário","Mode temporaire"],
  ["Motor navegador","Browser engine","Движок браузера","浏览器引擎","Motor do navegador","Moteur navigateur"],
  ["Alternar apariencia","Switch appearance","Сменить оформление","切换外观","Alternar aparência","Changer l’apparence"],
  ["Estudio HTTP","HTTP Studio","HTTP-студия","HTTP 工作台","Estúdio HTTP","Studio HTTP"],
  ["Construye y depura solicitudes, sin renunciar al control de cada parámetro.","Build and debug requests with full control of every parameter.","Создавайте и отлаживайте запросы с полным контролем параметров.","构建和调试请求，精细控制每个参数。","Crie e depure requisições com controle total dos parâmetros.","Créez et déboguez les requêtes en contrôlant chaque paramètre."],
  ["Editor avanzado","Advanced editor","Расширенный редактор","高级编辑器","Editor avançado","Éditeur avancé"],
  ["Nueva petición","New request","Новый запрос","新建请求","Nova requisição","Nouvelle requête"],
  ["Control de origen","Origin control","Контроль источника","源站控制","Controle de origem","Contrôle de l’origine"],
  ["URL completa o plantilla","Full URL or template","Полный URL или шаблон","完整 URL 或模板","URL completa ou modelo","URL complète ou modèle"],
  ["Método HTTP","HTTP method","Метод HTTP","HTTP 方法","Método HTTP","Méthode HTTP"],
  ["Método personalizado","Custom method","Пользовательский метод","自定义方法","Método personalizado","Méthode personnalisée"],
  ["Parámetros Query","Query parameters","Параметры запроса","查询参数","Parâmetros de consulta","Paramètres de requête"],
  ["nombre=valor por línea","name=value per line","имя=значение на строку","每行 名称=值","nome=valor por linha","nom=valeur par ligne"],
  ["Cookies manuales","Manual cookies","Ручные cookie","手动 Cookie","Cookies manuais","Cookies manuels"],
  ["requieren gateway local","require local gateway","нужен локальный шлюз","需要本地网关","requer gateway local","nécessitent une passerelle locale"],
  ["Tipo de cuerpo","Body type","Тип тела запроса","请求体类型","Tipo de corpo","Type de corps"],
  ["Sin cuerpo","No body","Без тела","无请求体","Sem corpo","Sans corps"],
  ["Texto / Raw","Text / Raw","Текст / Raw","文本 / 原始","Texto / Raw","Texte / Brut"],
  ["Binario Base64","Base64 binary","Двоичные данные Base64","Base64 二进制","Binário Base64","Binaire Base64"],
  ["Seguir redirecciones","Follow redirects","Следовать перенаправлениям","跟随重定向","Seguir redirecionamentos","Suivre les redirections"],
  ["Autorizar HTTP sin cifrar (laboratorio)","Allow unencrypted HTTP (lab)","Разрешить HTTP без шифрования (лаборатория)","允许未加密 HTTP（实验环境）","Permitir HTTP sem criptografia (laboratório)","Autoriser HTTP non chiffré (laboratoire)"],
  ["Preferir HTTP/1.1","Prefer HTTP/1.1","Предпочитать HTTP/1.1","优先 HTTP/1.1","Preferir HTTP/1.1","Préférer HTTP/1.1"],
  ["(solo Android)","(Android only)","(только Android)","（仅 Android）","(somente Android)","(Android uniquement)"],
  ["Tiempo máximo (segundos)","Timeout (seconds)","Тайм-аут (секунды)","超时（秒）","Tempo limite (segundos)","Délai d’attente (secondes)"],
  ["Motor de red","Network engine","Сетевой движок","网络引擎","Motor de rede","Moteur réseau"],
  ["Navegador / CORS","Browser / CORS","Браузер / CORS","浏览器 / CORS","Navegador / CORS","Navigateur / CORS"],
  ["Gateway local autorizado","Authorized local gateway","Разрешённый локальный шлюз","授权本地网关","Gateway local autorizado","Passerelle locale autorisée"],
  ["El navegador impone CORS y restringe headers. El gateway local opcional requiere configurar una lista de orígenes autorizados.","Browsers enforce CORS and restrict headers. The optional gateway requires an allowlist of authorized origins.","Браузер применяет CORS и ограничивает заголовки. Для шлюза нужен список разрешённых источников.","浏览器执行 CORS 并限制请求头。可选网关需要配置授权源站列表。","O navegador aplica CORS e restringe cabeçalhos. O gateway opcional exige uma lista de origens autorizadas.","Le navigateur applique CORS et limite les en-têtes. La passerelle facultative exige une liste d’origines autorisées."],
  ["Enviar petición","Send request","Отправить запрос","发送请求","Enviar requisição","Envoyer la requête"],
  ["Cancelar","Cancel","Отмена","取消","Cancelar","Annuler"],
  ["Formatear JSON","Format JSON","Форматировать JSON","格式化 JSON","Formatar JSON","Formater JSON"],
  ["Herramientas cURL","cURL tools","Инструменты cURL","cURL 工具","Ferramentas cURL","Outils cURL"],
  ["Importar comando (sin ejecutar shell)","Import command (without shell execution)","Импорт команды (без запуска оболочки)","导入命令（不执行 Shell）","Importar comando (sem executar shell)","Importer une commande (sans exécuter de shell)"],
  ["Importar cURL","Import cURL","Импорт cURL","导入 cURL","Importar cURL","Importer cURL"],
  ["Copiar cURL redactado","Copy redacted cURL","Копировать cURL без секретов","复制已脱敏的 cURL","Copiar cURL com dados ocultos","Copier le cURL expurgé"],
  ["Respuesta reciente","Latest response","Последний ответ","最近响应","Resposta recente","Dernière réponse"],
  ["Esperando una petición","Waiting for a request","Ожидание запроса","等待请求","Aguardando requisição","En attente d’une requête"],
  ["El estado, la latencia y la vista resumida aparecerán aquí.","Status, latency and response preview will appear here.","Здесь появятся статус, задержка и просмотр ответа.","此处显示状态、延迟和响应预览。","Status, latência e prévia aparecerão aqui.","Le statut, la latence et l’aperçu s’afficheront ici."],
  ["Abrir inspector completo","Open full inspector","Открыть полный инспектор","打开完整检查器","Abrir inspetor completo","Ouvrir l’inspecteur complet"],
  ["Variables dinámicas","Dynamic variables","Динамические переменные","动态变量","Variáveis dinâmicas","Variables dynamiques"],
  ["Gestionar entornos","Manage environments","Управлять окружениями","管理环境","Gerenciar ambientes","Gérer les environnements"],
  ["Colecciones y entornos","Collections and environments","Коллекции и окружения","集合与环境","Coleções e ambientes","Collections et environnements"],
  ["Solicitudes reutilizables y secretos cifrados en este navegador.","Reusable requests and encrypted secrets stored in this browser.","Повторно используемые запросы и зашифрованные секреты в браузере.","可复用请求与加密机密信息存储在此浏览器中。","Requisições reutilizáveis e segredos criptografados neste navegador.","Requêtes réutilisables et secrets chiffrés dans ce navigateur."],
  ["Almacenamiento cifrado","Encrypted storage","Зашифрованное хранилище","加密存储","Armazenamento criptografado","Stockage chiffré"],
  ["Contraseña del espacio local","Local workspace passphrase","Пароль локальной области","本地工作区密码","Senha do espaço local","Mot de passe de l’espace local"],
  ["Crear espacio cifrado","Create encrypted workspace","Создать зашифрованную область","创建加密工作区","Criar espaço criptografado","Créer un espace chiffré"],
  ["Desbloquear","Unlock","Разблокировать","解锁","Desbloquear","Déverrouiller"],
  ["Bloquear","Lock","Заблокировать","锁定","Bloquear","Verrouiller"],
  ["Descargar respaldo cifrado","Download encrypted backup","Скачать зашифрованную копию","下载加密备份","Baixar backup criptografado","Télécharger la sauvegarde chiffrée"],
  ["Importar respaldo cifrado","Import encrypted backup","Импорт зашифрованной копии","导入加密备份","Importar backup criptografado","Importer une sauvegarde chiffrée"],
  ["Guardar petición","Save request","Сохранить запрос","保存请求","Salvar requisição","Enregistrer la requête"],
  ["Nombre","Name","Имя","名称","Nome","Nom"],
  ["Colección","Collection","Коллекция","集合","Coleção","Collection"],
  ["Guardar petición actual","Save current request","Сохранить текущий запрос","保存当前请求","Salvar requisição atual","Enregistrer la requête actuelle"],
  ["Importar OpenAPI 3.x JSON","Import OpenAPI 3.x JSON","Импорт OpenAPI 3.x JSON","导入 OpenAPI 3.x JSON","Importar OpenAPI 3.x JSON","Importer OpenAPI 3.x JSON"],
  ["Especificación","Specification","Спецификация","规范","Especificação","Spécification"],
  ["Importar operaciones","Import operations","Импорт операций","导入操作","Importar operações","Importer les opérations"],
  ["Entornos y variables","Environments and variables","Окружения и переменные","环境与变量","Ambientes e variáveis","Environnements et variables"],
  ["Entorno activo","Active environment","Активное окружение","当前环境","Ambiente ativo","Environnement actif"],
  ["Nuevo entorno","New environment","Новое окружение","新环境","Novo ambiente","Nouvel environnement"],
  ["Crear","Create","Создать","创建","Criar","Créer"],
  ["Variable","Variable","Переменная","变量","Variável","Variable"],
  ["Valor privado","Private value","Секретное значение","私有值","Valor privado","Valeur privée"],
  ["Guardar variable","Save variable","Сохранить переменную","保存变量","Salvar variável","Enregistrer la variable"],
  ["Solicitudes guardadas","Saved requests","Сохранённые запросы","已保存请求","Requisições salvas","Requêtes enregistrées"],
  ["Pruebas de colecciones","Collection tests","Тесты коллекций","集合测试","Testes de coleções","Tests de collections"],
  ["Código HTTP esperado","Expected HTTP status","Ожидаемый статус HTTP","预期 HTTP 状态","Status HTTP esperado","Statut HTTP attendu"],
  ["Latencia máxima (ms)","Maximum latency (ms)","Максимальная задержка (мс)","最大延迟（毫秒）","Latência máxima (ms)","Latence maximale (ms)"],
  ["Ejecutar colección","Run collection","Запустить коллекцию","运行集合","Executar coleção","Exécuter la collection"],
  ["Explorar API","Explore API","Исследовать API","探索 API","Explorar API","Explorer l’API"],
  ["Rutas y parámetros candidatos","Candidate routes and parameters","Возможные маршруты и параметры","候选路由与参数","Rotas e parâmetros candidatos","Routes et paramètres candidats"],
  ["Rutas absolutas · una por línea","Absolute paths · one per line","Абсолютные пути · по одному в строке","绝对路径 · 每行一个","Rotas absolutas · uma por linha","Chemins absolus · un par ligne"],
  ["Añadir rutas de respuesta anterior","Add paths from previous response","Добавить пути из ответа","从上一响应添加路径","Adicionar rotas da resposta anterior","Ajouter les chemins de la réponse précédente"],
  ["Posibles parámetros · uno por línea","Candidate parameters · one per line","Возможные параметры · по одному в строке","候选参数 · 每行一个","Parâmetros candidatos · um por linha","Paramètres candidats · un par ligne"],
  ["Métodos HTTP","HTTP methods","Методы HTTP","HTTP 方法","Métodos HTTP","Méthodes HTTP"],
  ["Habilitar métodos que pueden modificar datos","Enable methods that can change data","Разрешить методы, меняющие данные","启用可能修改数据的方法","Habilitar métodos que podem alterar dados","Activer les méthodes qui peuvent modifier les données"],
  ["Confirmo que estoy autorizado a probar el origen seleccionado.","I confirm I am authorized to test this origin.","Подтверждаю право тестировать этот источник.","我确认已获授权测试此源站。","Confirmo que tenho autorização para testar esta origem.","Je confirme être autorisé à tester cette origine."],
  ["Ejecutar exploración","Run exploration","Запустить исследование","运行探索","Executar exploração","Lancer l’exploration"],
  ["Historial local","Local history","Локальная история","本地历史记录","Histórico local","Historique local"],
  ["Borrar historial","Clear history","Очистить историю","清除历史记录","Limpar histórico","Effacer l’historique"],
  ["Inspector completo","Full inspector","Полный инспектор","完整检查器","Inspetor completo","Inspecteur complet"],
  ["Solicitud enviada","Sent request","Отправленный запрос","已发送请求","Requisição enviada","Requête envoyée"],
  ["Respuesta recibida","Received response","Полученный ответ","收到的响应","Resposta recebida","Réponse reçue"],
  ["Sin petición.","No request yet.","Нет запроса.","暂无请求。","Sem requisição.","Aucune requête."],
  ["Sin respuesta.","No response yet.","Нет ответа.","暂无响应。","Sem resposta.","Aucune réponse."],
  ["Cuerpo de respuesta","Response body","Тело ответа","响应体","Corpo da resposta","Corps de la réponse"],
  ["Copiar cuerpo de respuesta","Copy response body","Копировать тело ответа","复制响应体","Copiar corpo da resposta","Copier le corps de la réponse"],
  ["Vista previa del cuerpo","Body preview","Предпросмотр тела","请求体预览","Prévia do corpo","Aperçu du corps"],
  ["Sin contenido.","No content.","Нет содержимого.","无内容。","Sem conteúdo.","Aucun contenu."],
  ["Seguridad","Security","Безопасность","安全","Segurança","Sécurité"],
  ["Headers por origen y uso opcional de cookies administradas por el navegador.","Origin-scoped headers and optional browser-managed cookies.","Заголовки по источникам и cookie, управляемые браузером.","按源站隔离的请求头及可选的浏览器 Cookie。","Cabeçalhos por origem e cookies opcionais do navegador.","En-têtes par origine et cookies facultatifs du navigateur."],
  ["Origen actual:","Current origin:","Текущий источник:","当前源站：","Origem atual:","Origine actuelle :"],
  ["Modo","Mode","Режим","模式","Modo","Mode"],
  ["Sin sesión","No session","Без сессии","无会话","Sem sessão","Sans session"],
  ["Memoria","Memory","Память","内存","Memória","Mémoire"],
  ["Persistente cifrada (headers)","Encrypted persistent (headers)","Постоянная зашифрованная (заголовки)","加密持久化（请求头）","Persistente criptografada (headers)","Persistante chiffrée (en-têtes)"],
  ["Headers del origen","Origin headers","Заголовки источника","源站请求头","Headers da origem","En-têtes de l’origine"],
  ["Guardar headers","Save headers","Сохранить заголовки","保存请求头","Salvar headers","Enregistrer les en-têtes"],
  ["Limpiar headers","Clear headers","Очистить заголовки","清除请求头","Limpar headers","Effacer les en-têtes"],
  ["Extensiones y capacidades","Extensions and capabilities","Расширения и возможности","扩展与功能","Extensões e recursos","Extensions et capacités"],
  ["Funciones integradas y limitaciones del cliente web.","Built-in features and browser limitations.","Встроенные функции и ограничения браузера.","内置功能和浏览器限制。","Recursos integrados e limitações do navegador.","Fonctions intégrées et limites du navigateur."],
  ["APOYA EL PROYECTO","SUPPORT THE PROJECT","ПОДДЕРЖАТЬ ПРОЕКТ","支持项目","APOIE O PROJETO","SOUTENEZ LE PROJET"],
  ["Donaciones voluntarias","Voluntary donations","Добровольные пожертвования","自愿捐赠","Doações voluntárias","Dons volontaires"],
  ["Reqestra permanece funcional sin donar; ninguna función requiere contribuciones.","Reqestra works without donations; no features require a contribution.","Reqestra работает без пожертвований: все функции доступны бесплатно.","不捐赠也可使用 Reqestra 的全部功能。","Reqestra funciona sem doações; nenhum recurso exige contribuição.","Reqestra fonctionne sans don ; aucune fonction ne nécessite une contribution."],
  ["Ayuda a mejorar Reqestra","Help improve Reqestra","Помогите улучшить Reqestra","帮助改进 Reqestra","Ajude a melhorar o Reqestra","Aidez à améliorer Reqestra"],
  ["Donar con PayPal ↗","Donate with PayPal ↗","Пожертвовать через PayPal ↗","通过 PayPal 捐赠 ↗","Doar com PayPal ↗","Faire un don avec PayPal ↗"],
  ["Código fuente","Source code","Исходный код","源代码","Código-fonte","Code source"],
  ["Más herramientas","More tools","Другие инструменты","更多工具","Mais ferramentas","Plus d’outils"],
  ["Secciones principales","Main sections","Основные разделы","主要部分","Seções principais","Sections principales"],
  ["Vista previa HTML aislada","Isolated HTML preview","Изолированный предпросмотр HTML","隔离的 HTML 预览","Prévia HTML isolada","Aperçu HTML isolé"],
  ["Abrir","Open","Открыть","打开","Abrir","Ouvrir"],
  ["Borrar","Delete","Удалить","删除","Excluir","Supprimer"],
  ["Eliminar","Remove","Удалить","移除","Remover","Supprimer"],
  ["Reutilizar URL redactada","Reuse redacted URL","Повторно использовать скрытый URL","重用已脱敏的 URL","Reutilizar URL ocultada","Réutiliser l’URL expurgée"],
  ["Sin variables definidas.","No variables defined.","Переменных нет.","尚未定义变量。","Nenhuma variável definida.","Aucune variable définie."],
  ["No hay peticiones guardadas.","No saved requests.","Нет сохранённых запросов.","没有已保存的请求。","Nenhuma requisição salva.","Aucune requête enregistrée."],
  ["No hay resultados.","No results.","Нет результатов.","无结果。","Sem resultados.","Aucun résultat."],
  ["Integrado / opcional","Built-in / optional","Встроено / опционально","内置 / 可选","Integrado / opcional","Intégré / facultatif"],
  ["No disponible","Not available","Недоступно","不可用","Indisponível","Indisponible"],
  ["Abrir extensión","Open extension","Открыть расширение","打开扩展","Abrir extensão","Ouvrir l’extension"],
  ["Idioma","Language","Язык","语言","Idioma","Langue"],
  ["Idioma de la interfaz","Interface language","Язык интерфейса","界面语言","Idioma da interface","Langue de l’interface"],
];
// Additional interface and Android cross-platform labels.
const additionalRows = [
  ["DIAGNÓSTICO","DIAGNOSTICS","ДИАГНОСТИКА","诊断","DIAGNÓSTICO","DIAGNOSTIC"],
  ["PREVISUALIZACIÓN","PREVIEW","ПРЕДПРОСМОТР","预览","PRÉ-VISUALIZAÇÃO","APERÇU"],
  ["ECOSISTEMA","ECOSYSTEM","ЭКОСИСТЕМА","生态系统","ECOSSISTEMA","ÉCOSYSTÈME"],
  ["ACTIVIDAD","ACTIVITY","АКТИВНОСТЬ","活动","ATIVIDADE","ACTIVITÉ"],
  ["SEGURIDAD","SECURITY","БЕЗОПАСНОСТЬ","安全","SEGURANÇA","SÉCURITÉ"],
  ["TU ESPACIO DE TRABAJO","YOUR WORKSPACE","ВАША РАБОЧАЯ ОБЛАСТЬ","您的工作区","SEU ESPAÇO DE TRABALHO","VOTRE ESPACE DE TRAVAIL"],
  ["Solo memoria","Memory only","Только память","仅内存","Somente memória","Mémoire uniquement"],
  ["Persistente cifrada","Encrypted persistent","Зашифрованное хранилище","加密持久化","Persistente criptografada","Persistance chiffrée"],
  ["Form URL encoded","Form URL-encoded","Форма URL-кодировка","表单 URL 编码","Formulário URL-encoded","Formulaire URL encodé"],
  ["Opciones","Options","Параметры","选项","Opções","Options"],
  ["Resultados","Results","Результаты","结果","Resultados","Résultats"],
  ["Progreso","Progress","Прогресс","进度","Progresso","Progression"],
  ["Query params · nombre=valor","Query parameters · name=value","Параметры · имя=значение","查询参数 · 名称=值","Parâmetros · nome=valor","Paramètres · nom=valeur"],
  ["100 % voluntario","100% voluntary","100% добровольно","完全自愿","100% voluntário","100 % volontaire"],
  ["Sin headers.","No headers.","Нет заголовков.","无请求头。","Sem headers.","Aucun en-tête."],
  ["(sin cuerpo)","(empty body)","(пустое тело)","（空请求体）","(sem corpo)","(corps vide)"],
];
const extraEn = [
 ["Navegación","Navigation"],
 ["La donación no desbloquea contenido, privilegios, insignias ni beneficios digitales.","Donations do not unlock content, privileges, badges or digital benefits."],
 ["El propietario debe configurar el identificador público del botón PayPal antes de activar donaciones.","The owner must configure the public PayPal button ID before enabling donations."],
 ["Si decides donar, la operación se completa exclusivamente en la página oficial de PayPal. Reqestra no recoge datos de tarjetas, identificadores de pago ni información financiera.","If you choose to donate, payment is handled entirely by PayPal. Reqestra never collects card details or financial information."],
 ["Las cookies HttpOnly y sus detalles no son accesibles mediante JavaScript. Al seleccionar sesiones, el navegador puede incluir cookies de acuerdo con sus propias políticas; Reqestra no puede eliminarlas.","JavaScript cannot access HttpOnly cookies. The browser may send its own cookies under its policies; Reqestra cannot delete them."],
 ["Solo método, URL redactada, código HTTP y latencia (hasta 50).","Method, redacted URL, HTTP status and latency only (up to 50)."],
 ["Solicitud redactada, cabeceras disponibles y respuesta HTTP.","Redacted request, available headers and HTTP response."],
 ["HTML con sandbox y política de contenido restrictiva; los demás formatos se muestran como texto.","Sandboxed HTML preview with strict content policy; other formats display as text."],
 ["Descubrimiento acotado sobre el mismo origen, con confirmación de autorización.","Limited same-origin discovery with explicit authorization."],
 ["El origen será la URL actual del Estudio. Máximo 8 rutas y 8 parámetros; no se siguen redirecciones.","The current Studio URL sets the origin. Up to 8 routes and 8 parameters; redirects are not followed."],
 ["Las diferencias detectadas no confirman que un parámetro sea oculto o vulnerable.","Observed differences do not prove that a parameter is hidden or vulnerable."],
 ["Límite de 24 peticiones · 1/segundo · sin seguimiento de redirecciones. POST/PUT/PATCH/DELETE pueden modificar o eliminar datos.","Up to 24 requests · 1/second · no redirects. POST/PUT/PATCH/DELETE can change or delete data."],
 ["Algunos métodos pueden modificar datos reales. Comprueba los permisos y el alcance antes de ejecutar.","Some methods change real data. Verify scope and permissions before running."],
 ["Hasta 20 solicitudes secuenciales con un segundo entre cada una.","Up to 20 sequential requests with one second between requests."],
 ["AES-GCM y una clave derivada de tu contraseña. Sin sincronización automática con Android.","AES-GCM with a passphrase-derived key. No automatic synchronization with Android."],
 ["Sin la contraseña, el contenido cifrado no se puede recuperar. El modo temporal no persiste después de recargar.","Encrypted data cannot be recovered without the passphrase. Temporary mode resets on reload."],
 [". Define las variables en Colecciones → Entornos. Se sustituyen al ejecutar y no aparecen en el historial.",". Define variables under Collections → Environments. They expand on execution and never appear in history."],
 ["Ejemplo:","Example:"],
 ["máximo 4 MiB","maximum 4 MiB"],
 ["HTTP STUDIO · WEB 3.3","HTTP STUDIO · WEB 3.4"],
 ["Reqestra Studio · 3.3.0","Reqestra Studio · 3.4.0"],
 ["Cliente local · Uso autorizado exclusivamente ·","Local client · Authorized use only ·"],
 ["Headers","Headers"],
 ["Body","Body"],
 ["Especificación","Specification"],
 ["Mínimo 10 caracteres","At least 10 characters"],
 ["Consultar perfil","Fetch profile"],
 ["No se mostrará después de guardar","Not shown after saving"],
 ["Recepción de respuesta","Response received"],
];
const catalogs = Object.fromEntries(LOCALES.map(l=>[l.code,new Map()]));
for(const [es,en,ru,zh,pt,fr] of [...rows,...additionalRows]){
  for(const [code,value] of [["en",en],["es",es],["ru",ru],["zh",zh],["pt",pt],["fr",fr]])
    catalogs[code].set(es,value);
}
for(const [es,en] of extraEn){
  catalogs.en.set(es,en);
  catalogs.es.set(es,es);
}
const originalText = new WeakMap();
const originalAttrs = new WeakMap();
let language="en";
export function normalizeLocale(code){
  const base=String(code||"").toLowerCase().split(/[-_]/)[0];
  return LOCALES.some(l=>l.code===base)?base:"en";
}
export function deviceLocale(){
  // Only the primary device language determines the default.
  // Do not pick a secondary language if the device's first choice is unsupported.
  const primary=navigator.languages?.[0]||navigator.language||"en";
  return normalizeLocale(primary);
}
export function getLocale(){return language;}
export function t(source,locale=language){
  const text=String(source??"");
  const entries=catalogs[locale]||catalogs.en;
  return entries.get(text)??catalogs.en.get(text)??text;
}
export function setLocale(next,{persist=true}={}){
  language=normalizeLocale(next);
  document.documentElement.lang=language==="zh"?"zh-Hans":language;
  if(persist)localStorage.setItem("reqestra.locale",language);
  translateDom();
  return language;
}
function translated(source){
  const exact=t(source);
  if(exact!==source || source==="")return exact;
  // Keep leading icons used by the navigation and call-to-action buttons.
  const icon=/^([⌘▤✓◇◷≡▣♧◈♥➤]\s+)(.*)$/u.exec(source);
  return icon?icon[1]+t(icon[2]):source;
}
function processText(node){
  if(!node.parentElement || node.parentElement.closest("pre,code,script,style,textarea,option[value=GET],option[value=POST]"))return;
  const value=node.nodeValue||"";
  let saved=originalText.get(node);
  if(!saved || saved.last!==value){saved={source:value,last:value};originalText.set(node,saved);}
  const trimmed=saved.source.trim();
  if(!trimmed)return;
  const translatedText=translated(trimmed);
  const next=saved.source.replace(trimmed,translatedText);
  if(value!==next){node.nodeValue=next;saved.last=next;}
}
function processAttrs(element){
  const attrNames=["placeholder","aria-label","title","content"];
  if(!originalAttrs.has(element))originalAttrs.set(element,new Map());
  const attrState=originalAttrs.get(element);
  for(const name of attrNames){
    if(!element.hasAttribute(name))continue;
    if(name==="content"&&!element.matches('meta[name="description"]'))continue;
    const value=element.getAttribute(name);
    let record=attrState.get(name);
    if(!record||record.last!==value){record={source:value,last:value};attrState.set(name,record);}
    const next=translated(record.source);
    if(value!==next){element.setAttribute(name,next);record.last=next;}
  }
}
export function translateDom(root=document.body){
  if(!root)return;
  const walker=document.createTreeWalker(root,NodeFilter.SHOW_TEXT);
  let node;
  while((node=walker.nextNode()))processText(node);
  const elements=[root,...root.querySelectorAll("[placeholder],[aria-label],[title],meta[name=description]")];
  elements.forEach(processAttrs);
}
export function initI18n(onChange=()=>{}){
  const select=document.getElementById("languageSelect");
  if(!select)throw Error("Language selector #languageSelect not found.");
  select.replaceChildren();
  for(const entry of LOCALES){
    const option=document.createElement("option");
    option.value=entry.code;option.textContent=entry.name;select.append(option);
  }
  const stored=localStorage.getItem("reqestra.locale");
  language=stored&&LOCALES.some(l=>l.code===stored)?stored:deviceLocale();
  select.value=language;
  select.addEventListener("change",()=>{
    setLocale(select.value);
    onChange(language);
  });
  setLocale(language,{persist:false});
  let pending=false;
  // Newly rendered cards, notices and test results get the same locale.
  const observer=new MutationObserver(()=>{
    if(pending)return;
    pending=true;
    queueMicrotask(()=>{pending=false;translateDom();});
  });
  observer.observe(document.body,{subtree:true,childList:true,characterData:true});
  return language;
}
