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
  ["Donar","Donate","Поддержать","捐赠","Doar","Faire un don"],
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
const completeRows = [
  ["REQUEST WORKBENCH","REQUEST WORKBENCH","РАБОЧЕЕ МЕСТО","请求工作台","ÁREA DE REQUISIÇÕES","ATELIER DES REQUÊTES"],
  ["WORKSPACE","WORKSPACE","РАБОЧАЯ ОБЛАСТЬ","工作区","ESPAÇO DE TRABALHO","ESPACE DE TRAVAIL"],
  ["AUTOMATION","AUTOMATION","АВТОМАТИЗАЦИЯ","自动化","AUTOMAÇÃO","AUTOMATISATION"],
  ["DISCOVERY LAB","DISCOVERY LAB","ИССЛЕДОВАНИЕ API","API 探索","LABORATÓRIO DE DESCOBERTA","LABORATOIRE D’EXPLORATION"],
  ["Secciones","Sections","Разделы","功能分区","Seções","Sections"],
  ["Interface language","Interface language","Язык интерфейса","界面语言","Idioma da interface","Langue de l’interface"],
  ["Seleccionado","Selected","Выбрано","已选中","Selecionado","Sélectionné"],
  ["Laboratorio","Laboratory","Лаборатория","实验室","Laboratório","Laboratoire"],
  ["Reqestra Studio: cliente HTTP web para inspección, colecciones, variables, pruebas y exploración autorizada.","Reqestra Studio: HTTP workbench for inspecting requests, collections, variables, testing and authorized exploration.","Reqestra Studio: HTTP-инструмент для анализа запросов, коллекций, переменных, тестов и разрешённого исследования.","Reqestra Studio：用于请求检查、集合、变量、测试和授权探索的 HTTP 工作台。","Reqestra Studio: ferramenta HTTP para inspeção, coleções, variáveis, testes e exploração autorizada.","Reqestra Studio : outil HTTP pour l’inspection, les collections, les variables, les tests et l’exploration autorisée."],
  ["El estado HTTP y la latencia aparecerán aquí.","HTTP status and latency will appear here.","Здесь появятся HTTP-статус и задержка.","HTTP 状态和延迟将在此显示。","O status HTTP e a latência aparecerão aqui.","Le statut HTTP et la latence apparaîtront ici."],
  ["Body truncado","Body truncated","Тело ответа обрезано","响应体已截断","Corpo truncado","Corps tronqué"],
  ["Protocolo: ","Protocol: ","Протокол: ","协议：","Protocolo: ","Protocole : "],
  [" (truncado)"," (truncated)"," (обрезано)","（已截断）"," (truncado)"," (tronqué)"],
  ["Valor protegido · no se muestra","Protected value · hidden","Защищённое значение · скрыто","受保护的值 · 已隐藏","Valor protegido · oculto","Valeur protégée · masquée"],
  ["Diferencia observada; verificar","Difference observed; verify","Обнаружено отличие; проверьте","发现差异；请核实","Diferença observada; verificar","Différence observée ; à vérifier"],
  ["Donaciones desactivadas: configura paypalHostedButtonId en web/config.js.","Donations disabled: configure paypalHostedButtonId in web/config.js.","Пожертвования отключены: настройте paypalHostedButtonId в web/config.js.","捐赠已禁用：请在 web/config.js 中配置 paypalHostedButtonId。","Doações desativadas: configure paypalHostedButtonId em web/config.js.","Dons désactivés : configurez paypalHostedButtonId dans web/config.js."],
  ["History is empty.","History is empty.","История пуста.","历史记录为空。","O histórico está vazio.","L’historique est vide."],
  ["No request yet.","No request yet.","Запрос ещё не отправлен.","尚无请求。","Ainda não há requisição.","Aucune requête pour le moment."],
  ["No response yet.","No response yet.","Ответа пока нет.","暂无响应。","Ainda não há resposta.","Pas encore de réponse."],
  ["Cifrado · desbloqueado","Encrypted · unlocked","Зашифровано · разблокировано","已加密 · 已解锁","Criptografado · desbloqueado","Chiffré · déverrouillé"],
  ["Cifrado · bloqueado","Encrypted · locked","Зашифровано · заблокировано","已加密 · 已锁定","Criptografado · bloqueado","Chiffré · verrouillé"],
  ["Gateway disponible","Gateway available","Шлюз доступен","网关可用","Gateway disponível","Passerelle disponible"],
  ["Invalid URL","Invalid URL","Недопустимый URL","URL 无效","URL inválida","URL invalide"],
  ["Headers de este origen limpiados. Las cookies del navegador se gestionan fuera de Reqestra.","Origin headers cleared. Browser cookies are managed outside Reqestra.","Заголовки источника очищены. Cookie браузера управляются вне Reqestra.","已清除源站请求头。浏览器 Cookie 由浏览器自行管理。","Headers da origem removidos. Cookies do navegador são gerenciados fora do Reqestra.","En-têtes de l’origine effacés. Les cookies sont gérés par le navigateur."],
  ["Redacted URL loaded; review query parameters before sending.","Redacted URL loaded; review query parameters before sending.","Скрытый URL загружен; проверьте параметры.","已加载脱敏 URL；发送前检查查询参数。","URL ocultada carregada; revise os parâmetros antes do envio.","URL expurgée chargée ; vérifiez les paramètres avant l’envoi."],
  ["JSON formateado.","JSON formatted.","JSON отформатирован.","JSON 已格式化。","JSON formatado.","JSON formaté."],
  ["cURL importado. Revisa los datos antes de enviar.","cURL imported. Review the request before sending.","cURL импортирован. Проверьте запрос перед отправкой.","已导入 cURL。发送前请检查请求。","cURL importado. Revise a requisição antes de enviar.","cURL importé. Vérifiez la requête avant l’envoi."],
  ["Comando cURL redactado copiado.","Redacted cURL command copied.","Команда cURL без секретов скопирована.","已复制脱敏的 cURL 命令。","Comando cURL ocultado copiado.","Commande cURL expurgée copiée."],
  ["Cuerpo copiado al portapapeles.","Response body copied to clipboard.","Тело ответа скопировано в буфер обмена.","响应体已复制到剪贴板。","Corpo copiado para a área de transferência.","Corps copié dans le presse-papiers."],
  ["Entorno creado.","Environment created.","Окружение создано.","环境已创建。","Ambiente criado.","Environnement créé."],
  ["Variable guardada. Su valor no se muestra.","Variable saved. Value is hidden.","Переменная сохранена. Значение скрыто.","变量已保存，值已隐藏。","Variável salva. Valor oculto.","Variable enregistrée. Valeur masquée."],
  ["Delete the saved request?","Delete the saved request?","Удалить сохранённый запрос?","删除已保存的请求？","Excluir a requisição salva?","Supprimer la requête enregistrée ?"],
  ["Clear local history?","Clear local history?","Очистить локальную историю?","清除本地历史记录？","Limpar o histórico local?","Effacer l’historique local ?"],
  ["Historial eliminado.","History cleared.","История очищена.","历史记录已清除。","Histórico apagado.","Historique effacé."],
  ["Invalid status or latency assertions.","Invalid status or latency assertions.","Недопустимая проверка статуса или задержки.","状态码或延迟断言无效。","Verificações de status ou latência inválidas.","Assertions de statut ou de latence invalides."],
  ["Exploration completed. Response changes are clues, not confirmed vulnerabilities.","Exploration completed. Response changes are clues, not confirmed vulnerabilities.","Исследование завершено. Отличия ответов — лишь признаки, не подтверждённые уязвимости.","探索完成。响应变化只是线索，并非已确认的漏洞。","Exploração concluída. Alterações nas respostas são indícios, não vulnerabilidades confirmadas.","Exploration terminée. Les différences sont des indices, pas des vulnérabilités confirmées."],
  ["Espacio cifrado creado. Guarda tu contraseña.","Encrypted workspace created. Keep your passphrase safe.","Зашифрованная область создана. Сохраните пароль.","加密工作区已创建。请妥善保管密码。","Espaço criptografado criado. Guarde sua senha.","Espace chiffré créé. Conservez votre mot de passe."],
  ["Espacio cifrado desbloqueado.","Encrypted workspace unlocked.","Зашифрованная область разблокирована.","加密工作区已解锁。","Espaço criptografado desbloqueado.","Espace chiffré déverrouillé."],
  ["Vault locked. Temporary memory cleared.","Vault locked. Temporary memory cleared.","Хранилище заблокировано. Временная память очищена.","保险库已锁定，临时内存已清除。","Cofre bloqueado. Memória temporária limpa.","Coffre verrouillé. Mémoire temporaire effacée."],
  ["Respaldo cifrado descargado.","Encrypted backup downloaded.","Зашифрованная копия скачана.","已下载加密备份。","Backup criptografado baixado.","Sauvegarde chiffrée téléchargée."],
  ["Importing will replace this browser's encrypted vault. Continue?","Importing will replace this browser's encrypted vault. Continue?","Импорт заменит зашифрованное хранилище браузера. Продолжить?","导入将替换此浏览器的加密保险库。继续？","A importação substituirá o cofre criptografado deste navegador. Continuar?","L’importation remplacera le coffre chiffré de ce navigateur. Continuer ?"],
  ["Backup imported. Unlock it with the original passphrase.","Backup imported. Unlock it with the original passphrase.","Копия импортирована. Откройте её исходным паролем.","已导入备份。请使用原始密码解锁。","Backup importado. Desbloqueie com a senha original.","Sauvegarde importée. Déverrouillez-la avec le mot de passe d’origine."],
  ["El gateway requiere ejecutar web/server.mjs y configurar REQESTRA_ALLOWED_ORIGINS.","The gateway requires web/server.mjs and REQESTRA_ALLOWED_ORIGINS configuration.","Для шлюза запустите web/server.mjs и настройте REQESTRA_ALLOWED_ORIGINS.","网关需要运行 web/server.mjs 并配置 REQESTRA_ALLOWED_ORIGINS。","O gateway exige executar web/server.mjs e configurar REQESTRA_ALLOWED_ORIGINS.","La passerelle nécessite web/server.mjs et la configuration de REQESTRA_ALLOWED_ORIGINS."],
  ["Create HTTP requests, set headers and edit bodies.","Create HTTP requests, set headers and edit bodies.","Создание HTTP-запросов, заголовков и тел.","创建 HTTP 请求、配置请求头与请求体。","Crie requisições HTTP, cabeçalhos e corpos.","Créer des requêtes HTTP, définir les en-têtes et les corps."],
  ["Environment variables","Environment variables","Переменные окружения","环境变量","Variáveis de ambiente","Variables d’environnement"],
  ["Manage local environments and query templates.","Manage local environments and query templates.","Управление локальными окружениями и шаблонами.","管理本地环境和查询模板。","Gerencie ambientes locais e modelos de consulta.","Gérer les environnements et les modèles de requête."],
  ["Encrypted collections","Encrypted collections","Зашифрованные коллекции","加密集合","Coleções criptografadas","Collections chiffrées"],
  ["Save and restore requests inside an encrypted vault.","Save and restore requests inside an encrypted vault.","Сохранение и восстановление запросов в зашифрованном хранилище.","在加密保险库中保存和恢复请求。","Salve e restaure requisições em um cofre criptografado.","Enregistrer et restaurer les requêtes dans un coffre chiffré."],
  ["Test assertions","Test assertions","Проверки тестов","测试断言","Verificações de testes","Assertions de tests"],
  ["Run saved collections with status and latency checks.","Run saved collections with status and latency checks.","Запуск коллекций с проверкой статуса и задержки.","运行已保存集合并检查状态码和延迟。","Execute coleções com verificações de status e latência.","Exécuter les collections avec vérification du statut et de la latence."],
  ["Endpoint explorer","Endpoint explorer","Исследователь эндпоинтов","端点探索器","Explorador de endpoints","Explorateur de points d’accès"],
  ["Explore authorized routes and candidate parameters.","Explore authorized routes and candidate parameters.","Исследование разрешённых маршрутов и параметров.","探索授权路由与候选参数。","Explore rotas autorizadas e parâmetros candidatos.","Explorer les routes autorisées et les paramètres candidats."],
  ["cURL tools","cURL tools","Инструменты cURL","cURL 工具箱","Ferramentas cURL","Outils cURL"],
  ["Import and export redacted requests.","Import and export redacted requests.","Импорт и экспорт запросов без секретов.","导入和导出脱敏请求。","Importe e exporte requisições ocultadas.","Importer et exporter des requêtes expurgées."],
  ["OpenAPI importer","OpenAPI importer","Импорт OpenAPI","OpenAPI 导入器","Importador OpenAPI","Importateur OpenAPI"],
  ["Import OpenAPI 3.x JSON operations.","Import OpenAPI 3.x JSON operations.","Импорт операций OpenAPI 3.x JSON.","导入 OpenAPI 3.x JSON 操作。","Importe operações OpenAPI 3.x JSON.","Importer des opérations OpenAPI 3.x JSON."],
  ["Sessions","Sessions","Сессии","会话","Sessões","Sessions"],
  ["Manage origin-specific headers and browser cookies.","Manage origin-specific headers and browser cookies.","Управление заголовками источника и cookie браузера.","管理按源站隔离的请求头与浏览器 Cookie。","Gerencie cabeçalhos por origem e cookies do navegador.","Gérer les en-têtes par origine et les cookies du navigateur."],
  ["Private gateway","Private gateway","Частный шлюз","私有网关","Gateway privado","Passerelle privée"],
  ["Opt-in local gateway for authorized origins.","Opt-in local gateway for authorized origins.","Локальный шлюз для разрешённых источников, включаемый вручную.","仅在启用后用于授权源站的本地网关。","Gateway local opcional para origens autorizadas.","Passerelle locale facultative pour les origines autorisées."],
  ["GraphQL schema explorer","GraphQL schema explorer","Обозреватель схем GraphQL","GraphQL 模式探索器","Explorador de esquemas GraphQL","Explorateur de schémas GraphQL"],
  ["Planned; no implementation yet.","Planned; not implemented yet.","Запланировано; пока не реализовано.","已计划，尚未实现。","Planejado; ainda não implementado.","Prévu ; pas encore implémenté."],
  ["External proxy connector","External proxy connector","Внешний прокси-коннектор","外部代理连接器","Conector de proxy externo","Connecteur proxy externe"],
  ["Third-party plug-ins","Third-party plug-ins","Сторонние плагины","第三方插件","Plugins de terceiros","Extensions tierces"],
  ["Not supported; no remote code runs.","Not supported; no remote code runs.","Не поддерживается; удалённый код не запускается.","不支持；不会执行远程代码。","Não suportado; nenhum código remoto é executado.","Non pris en charge ; aucun code distant n’est exécuté."],
];
const validationRows = [
  ["Cancelado.","Cancelled.","Отменено.","已取消。","Cancelado.","Annulé."],
  ["Selecciona el tipo de cuerpo JSON.","Select JSON body type.","Выберите тип тела JSON.","请选择 JSON 请求体类型。","Selecione o tipo de corpo JSON.","Sélectionnez le type de corps JSON."],
  ["No existe respuesta.","No response exists.","Ответ отсутствует.","没有可用响应。","Não há resposta.","Aucune réponse disponible."],
  ["El entorno necesita nombre de 1 a 40 caracteres.","Environment name must contain 1–40 characters.","Имя окружения должно содержать 1–40 символов.","环境名称必须包含 1–40 个字符。","O nome do ambiente deve ter de 1 a 40 caracteres.","Le nom de l’environnement doit comporter 1 à 40 caractères."],
  ["El entorno ya existe.","Environment already exists.","Окружение уже существует.","环境已存在。","O ambiente já existe.","L’environnement existe déjà."],
  ["Variable demasiado larga.","Variable value is too long.","Значение переменной слишком длинное.","变量值过长。","O valor da variável é muito longo.","La valeur de la variable est trop longue."],
  ["Pruebas canceladas.","Tests cancelled.","Тесты отменены.","测试已取消。","Testes cancelados.","Tests annulés."],
  ["No se encontraron rutas sugeridas del mismo origen.","No same-origin route suggestions found.","Подсказки путей того же источника не найдены.","未发现同源路由建议。","Nenhuma rota sugerida da mesma origem encontrada.","Aucune suggestion de route de même origine trouvée."],
  ["Demasiadas cookies manuales.","Too many manual cookies.","Слишком много cookie, введённых вручную.","手动 Cookie 数量过多。","Muitos cookies manuais.","Trop de cookies manuels."],
  ["Gateway: respuesta ","Gateway: response ","Шлюз: ответ ","网关：响应 ","Gateway: resposta ","Passerelle : réponse "],
  ["El navegador ha bloqueado los detalles del redirect (opaqueredirect).","Browser blocked redirect details (opaqueredirect).","Браузер заблокировал сведения о перенаправлении (opaqueredirect).","浏览器阻止了重定向详情（opaqueredirect）。","O navegador bloqueou os detalhes do redirecionamento (opaqueredirect).","Le navigateur a bloqué les détails de la redirection (opaqueredirect)."],
  ["Respuesta opaca por restricciones CORS.","Opaque response due to CORS restrictions.","Непрозрачный ответ из-за ограничений CORS.","由于 CORS 限制，响应不可见。","Resposta opaca devido a restrições CORS.","Réponse opaque en raison des restrictions CORS."],
  ["La solicitud no pudo completarse: comprueba CORS, TLS, conectividad y los headers permitidos por el navegador.","Request failed: check CORS, TLS, connectivity and browser-allowed headers.","Запрос не выполнен: проверьте CORS, TLS, сеть и заголовки браузера.","请求失败：请检查 CORS、TLS、网络连接及浏览器允许的请求头。","A requisição falhou: verifique CORS, TLS, conectividade e cabeçalhos permitidos.","Requête échouée : vérifiez CORS, TLS, la connexion et les en-têtes autorisés."],
  ["Header de sesión: Nombre: valor.","Session header: Name: value.","Заголовок сессии: Имя: значение.","会话请求头：名称: 值。","Cabeçalho de sessão: Nome: valor.","En-tête de session : Nom: valeur."],
  ["No hay archivo cifrado.","No encrypted file.","Нет зашифрованного файла.","没有加密文件。","Nenhum arquivo criptografado.","Aucun fichier chiffré."],
  ["Respaldo demasiado grande.","Backup is too large.","Резервная копия слишком большая.","备份文件过大。","Backup muito grande.","Sauvegarde trop volumineuse."],
  ["Espacio de trabajo demasiado grande.","Workspace is too large.","Рабочая область слишком большая.","工作区数据过大。","Espaço de trabalho muito grande.","Espace de travail trop volumineux."],
  ["Debes desbloquear el espacio cifrado.","Unlock the encrypted workspace first.","Сначала разблокируйте зашифрованную область.","请先解锁加密工作区。","Desbloqueie o espaço criptografado primeiro.","Déverrouillez d’abord l’espace chiffré."],
  ["No hay espacio cifrado.","No encrypted workspace.","Зашифрованная область отсутствует.","没有加密工作区。","Nenhum espaço criptografado.","Aucun espace chiffré."],
  ["Espacio de trabajo incompatible.","Incompatible workspace.","Несовместимая рабочая область.","工作区不兼容。","Espaço de trabalho incompatível.","Espace de travail incompatible."],
  ["Enter a valid URL in Studio.","Enter a valid URL in Studio.","Введите допустимый URL в студии.","请在工作台中输入有效 URL。","Informe uma URL válida no Estúdio.","Saisissez une URL valide dans le Studio."],
  ["Select a session mode.","Select a session mode.","Выберите режим сессии.","请选择会话模式。","Selecione um modo de sessão.","Sélectionnez un mode de session."],
  ["To persist headers, first create or unlock an encrypted vault.","To persist headers, first create or unlock an encrypted vault.","Для сохранения заголовков создайте или откройте зашифрованное хранилище.","要持久保存请求头，请先创建或解锁加密保险库。","Para salvar os cabeçalhos, crie ou desbloqueie um cofre criptografado.","Pour conserver les en-têtes, créez ou déverrouillez un coffre chiffré."],
  ["Another operation is already running.","Another operation is already running.","Другая операция уже выполняется.","已有操作正在运行。","Outra operação está em execução.","Une autre opération est déjà en cours."],
  ["Maximum 12 environments.","Maximum 12 environments.","Максимум 12 окружений.","最多 12 个环境。","Máximo de 12 ambientes.","12 environnements maximum."],
  ["Invalid variable name.","Invalid variable name.","Недопустимое имя переменной.","变量名无效。","Nome de variável inválido.","Nom de variable invalide."],
  ["Invalid request or collection name.","Invalid request or collection name.","Недопустимое имя запроса или коллекции.","请求或集合名称无效。","Nome de requisição ou coleção inválido.","Nom de requête ou de collection invalide."],
  ["Maximum 120 saved requests.","Maximum 120 saved requests.","Максимум 120 сохранённых запросов.","最多保存 120 个请求。","Máximo de 120 requisições salvas.","120 requêtes enregistrées maximum."],
  ["Import would exceed 120 requests.","Import would exceed 120 requests.","Импорт превысит 120 запросов.","导入后将超过 120 个请求。","A importação excederia 120 requisições.","L’importation dépasserait 120 requêtes."],
  ["Choose a collection with 1 to 20 requests.","Choose a collection with 1 to 20 requests.","Выберите коллекцию из 1–20 запросов.","请选择包含 1 至 20 个请求的集合。","Escolha uma coleção com 1 a 20 requisições.","Choisissez une collection de 1 à 20 requêtes."],
  ["Run a request in Studio first.","Run a request in Studio first.","Сначала выполните запрос в студии.","请先在工作台中运行请求。","Execute primeiro uma requisição no Estúdio.","Exécutez d’abord une requête dans le Studio."],
  ["Confirm you are authorized to test this origin.","Confirm you are authorized to test this origin.","Подтвердите разрешение тестировать этот источник.","请确认您已获授权测试此源站。","Confirme sua autorização para testar esta origem.","Confirmez votre autorisation de tester cette origine."],
  ["Exploration cancelled.","Exploration cancelled.","Исследование отменено.","探索已取消。","Exploração cancelada.","Exploration annulée."],
  ["Invalid manual cookie.","Invalid manual cookie.","Недопустимая cookie, введённая вручную.","手动 Cookie 无效。","Cookie manual inválido.","Cookie manuel invalide."],
  ["Request timed out.","Request timed out.","Время ожидания запроса истекло.","请求超时。","Tempo limite da requisição excedido.","Délai d’attente de la requête dépassé."],
  ["Request cancelled or timed out.","Request cancelled or timed out.","Запрос отменён или истёк тайм-аут.","请求已取消或超时。","Requisição cancelada ou com tempo esgotado.","Requête annulée ou délai dépassé."],
  ["Maximum 100 session headers.","Maximum 100 session headers.","Максимум 100 заголовков сессии.","最多 100 个会话请求头。","Máximo de 100 cabeçalhos de sessão.","100 en-têtes de session maximum."],
  ["Invalid session header.","Invalid session header.","Недопустимый заголовок сессии.","会话请求头无效。","Cabeçalho de sessão inválido.","En-tête de session invalide."],
  ["Invalid encrypted Reqestra vault.","Invalid encrypted Reqestra vault.","Недопустимое зашифрованное хранилище Reqestra.","Reqestra 加密保险库无效。","Cofre criptografado do Reqestra inválido.","Coffre chiffré Reqestra invalide."],
  ["Vault passphrase must be at least 10 characters.","Vault passphrase must be at least 10 characters.","Пароль хранилища должен содержать не менее 10 символов.","保险库密码至少需要 10 个字符。","A senha do cofre deve ter pelo menos 10 caracteres.","Le mot de passe du coffre doit contenir au moins 10 caractères."],
  ["A vault already exists; unlock or export it before replacing it.","A vault already exists; unlock or export it before replacing it.","Хранилище уже существует; разблокируйте или экспортируйте его.","保险库已存在；请先解锁或导出。","Um cofre já existe; desbloqueie ou exporte antes de substituí-lo.","Un coffre existe déjà ; déverrouillez-le ou exportez-le avant de le remplacer."],
  ["Wrong passphrase or damaged encrypted data.","Wrong passphrase or damaged encrypted data.","Неверный пароль или повреждённые данные.","密码错误或加密数据已损坏。","Senha incorreta ou dados criptografados danificados.","Mot de passe incorrect ou données chiffrées endommagées."],
];
const coreRows = [
  ["Template too long.","Template too long.","Шаблон слишком большой.","模板过长。","Modelo muito longo.","Modèle trop long."],
  ["Template expansion too long.","Template expansion too long.","Результат шаблона слишком большой.","展开后的模板过长。","Expansão do modelo muito longa.","Expansion du modèle trop longue."],
  ["Query too large.","Query too large.","Слишком длинные параметры запроса.","查询参数过长。","Consulta muito grande.","Requête trop volumineuse."],
  ["Maximum 40 query parameters.","Maximum 40 query parameters.","Максимум 40 параметров запроса.","最多 40 个查询参数。","Máximo de 40 parâmetros de consulta.","40 paramètres de requête maximum."],
  ["Headers: LF newlines only.","Headers: LF newlines only.","Заголовки: только переводы строк LF.","请求头：仅允许 LF 换行。","Cabeçalhos: apenas quebras LF.","En-têtes : sauts de ligne LF uniquement."],
  ["Maximum 100 headers.","Maximum 100 headers.","Максимум 100 заголовков.","最多 100 个请求头。","Máximo de 100 cabeçalhos.","100 en-têtes maximum."],
  ["Use a full HTTP(S) URL without credentials or fragments.","Use a full HTTP(S) URL without credentials or fragments.","Укажите полный HTTP(S) URL без учётных данных и фрагментов.","请使用不含凭据或片段的完整 HTTP(S) URL。","Use uma URL HTTP(S) completa, sem credenciais ou fragmentos.","Utilisez une URL HTTP(S) complète, sans identifiants ni fragments."],
  ["Invalid HTTP(S) URL or embedded credentials/fragment.","Invalid HTTP(S) URL or embedded credentials/fragment.","Недопустимый HTTP(S) URL или встроенные учётные данные/фрагмент.","HTTP(S) URL 无效或包含嵌入凭据/片段。","URL HTTP(S) inválida ou com credenciais/fragmento.","URL HTTP(S) invalide ou avec identifiants/fragment."],
  ["URL too long.","URL too long.","URL слишком длинный.","URL 过长。","URL muito longa.","URL trop longue."],
  ["HTTP requires explicit lab approval.","HTTP requires explicit lab approval.","Для HTTP требуется разрешение лабораторного режима.","HTTP 需要明确启用实验模式。","HTTP exige aprovação explícita de laboratório.","HTTP nécessite l’autorisation explicite du mode laboratoire."],
  ["Invalid HTTP method.","Invalid HTTP method.","Недопустимый HTTP-метод.","HTTP 方法无效。","Método HTTP inválido.","Méthode HTTP invalide."],
  ["Invalid body type.","Invalid body type.","Недопустимый тип тела.","请求体类型无效。","Tipo de corpo inválido.","Type de corps invalide."],
  ["Timeout must be within 1–120 seconds.","Timeout must be within 1–120 seconds.","Тайм-аут: от 1 до 120 секунд.","超时必须在 1–120 秒内。","Tempo limite entre 1 e 120 segundos.","Délai d’attente compris entre 1 et 120 secondes."],
  ["Invalid Base64.","Invalid Base64.","Недопустимый Base64.","Base64 无效。","Base64 inválido.","Base64 invalide."],
  ["Body exceeds 5 MiB.","Body exceeds 5 MiB.","Тело превышает 5 МиБ.","请求体超过 5 MiB。","O corpo excede 5 MiB.","Le corps dépasse 5 Mio."],
  ["Browser cannot send manual Cookie. Use the local gateway or browser-managed cookies.","Browser cannot send manual Cookie. Use the local gateway or browser-managed cookies.","Браузер не может отправить cookie вручную. Используйте шлюз или cookie браузера.","浏览器无法发送手动 Cookie。请使用本地网关或浏览器管理的 Cookie。","O navegador não envia cookies manuais. Use o gateway ou cookies do navegador.","Le navigateur ne peut pas envoyer de cookie manuel. Utilisez la passerelle ou les cookies du navigateur."],
  ["OpenAPI exceeds 256 KiB.","OpenAPI exceeds 256 KiB.","OpenAPI превышает 256 КиБ.","OpenAPI 超过 256 KiB。","OpenAPI excede 256 KiB.","OpenAPI dépasse 256 Kio."],
  ["Only OpenAPI 3.x JSON is supported.","Only OpenAPI 3.x JSON is supported.","Поддерживается только OpenAPI 3.x JSON.","仅支持 OpenAPI 3.x JSON。","Apenas OpenAPI 3.x JSON é suportado.","Seul OpenAPI 3.x JSON est pris en charge."],
  ["OpenAPI requires an absolute HTTP(S) server.","OpenAPI requires an absolute HTTP(S) server.","OpenAPI требует абсолютный HTTP(S) сервер.","OpenAPI 需要绝对 HTTP(S) 服务器地址。","OpenAPI exige servidor HTTP(S) absoluto.","OpenAPI nécessite un serveur HTTP(S) absolu."],
  ["OpenAPI exceeds 50 operations.","OpenAPI exceeds 50 operations.","OpenAPI превышает 50 операций.","OpenAPI 超过 50 个操作。","OpenAPI excede 50 operações.","OpenAPI dépasse 50 opérations."],
  ["No compatible operations found.","No compatible operations found.","Совместимых операций нет.","未找到兼容操作。","Nenhuma operação compatível encontrada.","Aucune opération compatible trouvée."],
  ["Invalid origin.","Invalid origin.","Недопустимый источник.","源站无效。","Origem inválida.","Origine invalide."],
  ["Candidate lists too large.","Candidate lists too large.","Списки кандидатов слишком большие.","候选列表过大。","Listas de candidatos muito grandes.","Listes de candidats trop longues."],
  ["Maximum 8 routes and 8 parameters.","Maximum 8 routes and 8 parameters.","Максимум 8 путей и 8 параметров.","最多 8 个路由和 8 个参数。","Máximo de 8 rotas e 8 parâmetros.","8 routes et 8 paramètres maximum."],
  ["Unauthorized methods.","Unauthorized methods.","Недопустимые методы.","未经授权的方法。","Métodos não autorizados.","Méthodes non autorisées."],
  ["Invalid candidate parameter.","Invalid candidate parameter.","Недопустимый параметр-кандидат.","候选参数无效。","Parâmetro candidato inválido.","Paramètre candidat invalide."],
  ["Maximum 24 requests.","Maximum 24 requests.","Максимум 24 запроса.","最多 24 个请求。","Máximo de 24 requisições.","24 requêtes maximum."],
  ["Cross-origin requests are not allowed.","Cross-origin requests are not allowed.","Запросы на другой источник запрещены.","不允许跨源请求。","Requisições entre origens não são permitidas.","Les requêtes vers une autre origine sont interdites."],
  ["cURL input too long.","cURL input too long.","Команда cURL слишком длинная.","cURL 输入过长。","Comando cURL muito longo.","Commande cURL trop longue."],
  ["Paste a simple cURL command.","Paste a simple cURL command.","Вставьте простую команду cURL.","请粘贴简单的 cURL 命令。","Cole um comando cURL simples.","Collez une commande cURL simple."],
  ["Insecure TLS options are not imported.","Insecure TLS options are not imported.","Небезопасные параметры TLS не импортируются.","不会导入不安全的 TLS 选项。","Opções TLS inseguras não são importadas.","Les options TLS non sécurisées ne sont pas importées."],
  ["Unrecognized cURL argument.","Unrecognized cURL argument.","Неизвестный аргумент cURL.","无法识别的 cURL 参数。","Argumento cURL desconhecido.","Argument cURL inconnu."],
  ["Missing URL.","Missing URL.","Отсутствует URL.","缺少 URL。","URL ausente.","URL manquante."],
];
const metadataRows = [
  ["HTTP Studio","HTTP Studio","HTTP-студия","HTTP 工作台","Estúdio HTTP","Studio HTTP"],
  ["Duration: ","Duration: ","Длительность: ","耗时：","Duração: ","Durée : "],
  ["Bytes: ","Bytes: ","Байт: ","字节：","Bytes: ","Octets : "],
  ["(sin contenido)","(no content)","(нет содержимого)","（无内容）","(sem conteúdo)","(aucun contenu)"],
  ["You will be taken to PayPal's official site. No payments are processed within Reqestra.","You will be taken to PayPal's official site. No payments are processed within Reqestra.","Вы перейдёте на официальный сайт PayPal. Reqestra не обрабатывает платежи.","您将跳转到 PayPal 官方网站。Reqestra 不处理付款。","Você será redirecionado ao site oficial do PayPal. Nenhum pagamento é processado no Reqestra.","Vous serez redirigé vers le site officiel de PayPal. Aucun paiement n’est traité par Reqestra."],
];
const brandRows = [
  ["HTTP STUDIO · WEB 3.5","HTTP STUDIO · WEB 3.5","HTTP-СТУДИЯ · WEB 3.5","HTTP 工作台 · WEB 3.5","ESTÚDIO HTTP · WEB 3.5","STUDIO HTTP · WEB 3.5"],
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
const extraLocales = new Map([
  ["Navegación", ["Навигация","导航","Navegação","Navigation"]],
  ["La donación no desbloquea contenido, privilegios, insignias ni beneficios digitales.", ["Пожертвование не открывает контент, привилегии или цифровые бонусы.","捐赠不会解锁内容、权限、徽章或数字权益。","A doação não desbloqueia conteúdo, privilégios ou benefícios digitais.","Les dons ne débloquent ni contenu, privilèges, badges ni avantages numériques."]],
  ["El propietario debe configurar el identificador público del botón PayPal antes de activar donaciones.", ["Владелец должен настроить публичный идентификатор кнопки PayPal.","启用捐赠前，管理员必须配置公开的 PayPal 按钮 ID。","O proprietário deve configurar o ID público do botão PayPal antes de ativar doações.","Le propriétaire doit configurer l’identifiant public du bouton PayPal avant d’activer les dons."]],
  ["Si decides donar, la operación se completa exclusivamente en la página oficial de PayPal. Reqestra no recoge datos de tarjetas, identificadores de pago ni información financiera.", ["Пожертвование оформляется только на официальном сайте PayPal. Reqestra не собирает данные карт или платежей.","如果您选择捐赠，交易仅在 PayPal 官方网站完成。Reqestra 不收集卡号、支付凭据或财务信息。","Se você doar, o pagamento ocorre apenas no site oficial do PayPal. O Reqestra não coleta dados de cartão ou pagamento.","Si vous faites un don, le paiement se fait uniquement sur le site officiel de PayPal. Reqestra ne recueille aucune donnée bancaire."]],
  ["Las cookies HttpOnly y sus detalles no son accesibles mediante JavaScript. Al seleccionar sesiones, el navegador puede incluir cookies de acuerdo con sus propias políticas; Reqestra no puede eliminarlas.", ["JavaScript не имеет доступа к HttpOnly cookie. Браузер может отправлять cookie согласно своей политике; Reqestra не может их удалить.","JavaScript 无法访问 HttpOnly Cookie。浏览器可能根据自身策略发送 Cookie；Reqestra 无法删除它们。","O JavaScript não acessa cookies HttpOnly. O navegador pode enviar cookies segundo suas regras; o Reqestra não pode excluí-los.","JavaScript n’accède pas aux cookies HttpOnly. Le navigateur peut les transmettre selon ses règles ; Reqestra ne peut pas les supprimer."]],
  ["Solo método, URL redactada, código HTTP y latencia (hasta 50).", ["Только метод, скрытый URL, код HTTP и задержка (до 50).","仅保存方法、脱敏 URL、HTTP 状态码和延迟（最多 50 条）。","Apenas método, URL ocultada, código HTTP e latência (até 50).","Uniquement méthode, URL expurgée, code HTTP et latence (50 maximum)."]],
  ["Solicitud redactada, cabeceras disponibles y respuesta HTTP.", ["Запрос без секретов, доступные заголовки и HTTP-ответ.","脱敏后的请求、可用响应头和 HTTP 响应。","Requisição ocultada, cabeçalhos disponíveis e resposta HTTP.","Requête expurgée, en-têtes disponibles et réponse HTTP."]],
  ["HTML con sandbox y política de contenido restrictiva; los demás formatos se muestran como texto.", ["HTML в песочнице со строгой политикой; остальные форматы отображаются как текст.","HTML 在沙箱及严格内容策略下预览；其他格式以文本显示。","HTML em sandbox com política restritiva; outros formatos são exibidos como texto.","HTML isolé avec une politique de contenu stricte ; les autres formats sont affichés en texte."]],
  ["Descubrimiento acotado sobre el mismo origen, con confirmación de autorización.", ["Ограниченное исследование того же источника с подтверждением разрешения.","仅在同源范围内进行有限探索，并要求确认授权。","Exploração limitada à mesma origem, com confirmação de autorização.","Exploration limitée à la même origine, avec confirmation d’autorisation."]],
  ["El origen será la URL actual del Estudio. Máximo 8 rutas y 8 parámetros; no se siguen redirecciones.", ["Источник задаёт текущий URL студии. До 8 путей и 8 параметров; без перенаправлений.","以工作台当前 URL 为源站。最多 8 个路径和 8 个参数；不跟随重定向。","A origem será a URL atual do Estúdio. Até 8 rotas e 8 parâmetros; sem redirecionamentos.","L’origine est l’URL actuelle du Studio. Jusqu’à 8 routes et 8 paramètres ; sans redirection."]],
  ["Las diferencias detectadas no confirman que un parámetro sea oculto o vulnerable.", ["Обнаруженные отличия не доказывают наличие скрытого или уязвимого параметра.","检测到差异并不证明参数是隐藏的或存在漏洞。","As diferenças detectadas não comprovam parâmetros ocultos ou vulneráveis.","Les différences observées ne prouvent pas qu’un paramètre est caché ou vulnérable."]],
  ["Límite de 24 peticiones · 1/segundo · sin seguimiento de redirecciones. POST/PUT/PATCH/DELETE pueden modificar o eliminar datos.", ["До 24 запросов · 1/сек · без перенаправлений. POST/PUT/PATCH/DELETE могут изменять или удалять данные.","最多 24 个请求 · 每秒 1 个 · 不跟随重定向。POST/PUT/PATCH/DELETE 可能修改或删除数据。","Limite de 24 requisições · 1/s · sem redirecionamentos. POST/PUT/PATCH/DELETE podem alterar ou excluir dados.","24 requêtes max. · 1/s · sans redirection. POST/PUT/PATCH/DELETE peuvent modifier ou supprimer des données."]],
  ["Algunos métodos pueden modificar datos reales. Comprueba los permisos y el alcance antes de ejecutar.", ["Некоторые методы изменяют реальные данные. Проверьте разрешение и область тестирования.","某些方法可能更改真实数据。执行前请确认授权范围。","Alguns métodos podem alterar dados reais. Verifique a autorização e o escopo.","Certaines méthodes peuvent modifier des données réelles. Vérifiez les autorisations et le périmètre."]],
  ["Hasta 20 solicitudes secuenciales con un segundo entre cada una.", ["До 20 последовательных запросов с интервалом в секунду.","最多 20 个顺序请求，每个请求间隔 1 秒。","Até 20 requisições sequenciais, com um segundo de intervalo.","Jusqu’à 20 requêtes successives, espacées d’une seconde."]],
  ["AES-GCM y una clave derivada de tu contraseña. Sin sincronización automática con Android.", ["AES-GCM с ключом из пароля. Без автоматической синхронизации с Android.","采用 AES-GCM 和密码派生密钥加密，不会自动与 Android 同步。","AES-GCM com chave derivada da senha. Sem sincronização automática com Android.","AES-GCM avec une clé dérivée du mot de passe. Pas de synchronisation automatique avec Android."]],
  ["Sin la contraseña, el contenido cifrado no se puede recuperar. El modo temporal no persiste después de recargar.", ["Без пароля зашифрованные данные не восстановить. Временный режим сбрасывается при перезагрузке.","没有密码便无法恢复加密数据。刷新后临时模式的数据不会保留。","Sem a senha, os dados criptografados não podem ser recuperados. O modo temporário é apagado ao recarregar.","Sans mot de passe, les données chiffrées sont irrécupérables. Le mode temporaire est réinitialisé à l’actualisation."]],
  [". Define las variables en Colecciones → Entornos. Se sustituyen al ejecutar y no aparecen en el historial.", [". Задайте переменные в Коллекции → Окружения. Они подставляются при запуске и не сохраняются в истории。",". 在“集合 → 环境”中定义变量。运行时会替换变量，历史记录不会保存其值。",". Defina variáveis em Coleções → Ambientes. Elas são substituídas na execução e não aparecem no histórico.",". Définissez les variables dans Collections → Environnements. Elles sont remplacées à l’exécution et absentes de l’historique."]],
  ["Ejemplo:", ["Пример:","示例：","Exemplo:","Exemple :"]],
  ["máximo 4 MiB", ["до 4 МиБ","最多 4 MiB","máximo 4 MiB","4 Mio maximum"]],
  ["HTTP STUDIO · WEB 3.3", ["HTTP-СТУДИЯ · WEB 3.4","HTTP 工作台 · WEB 3.4","ESTÚDIO HTTP · WEB 3.4","STUDIO HTTP · WEB 3.4"]],
  ["Reqestra Studio · 3.3.0", ["Reqestra Studio · 3.4.0","Reqestra Studio · 3.4.0","Reqestra Studio · 3.4.0","Reqestra Studio · 3.4.0"]],
  ["Cliente local · Uso autorizado exclusivamente ·", ["Локальный клиент · Только разрешённое использование ·","本地客户端 · 仅限授权使用 ·","Cliente local · Somente uso autorizado ·","Client local · Utilisation autorisée uniquement ·"]],
  ["Headers", ["Заголовки","请求头","Cabeçalhos","En-têtes"]],
  ["Body", ["Тело запроса","请求体","Corpo","Corps"]],
  ["Especificación", ["Спецификация","规范","Especificação","Spécification"]],
  ["Mínimo 10 caracteres", ["Минимум 10 символов","至少 10 个字符","Mínimo de 10 caracteres","10 caractères minimum"]],
  ["Consultar perfil", ["Запросить профиль","查询资料","Consultar perfil","Consulter le profil"]],
  ["No se mostrará después de guardar", ["После сохранения значение скрыто","保存后不再显示","Não será exibido após salvar","Ne sera plus affiché après enregistrement"]],
  ["Recepción de respuesta", ["Получен ответ","接收响应","Recebimento da resposta","Réception de la réponse"]],
]);
const catalogs = Object.fromEntries(LOCALES.map(l=>[l.code,new Map()]));
for(const [es,en,ru,zh,pt,fr] of [...rows,...additionalRows,...completeRows,...validationRows,...coreRows,...metadataRows,...brandRows]){
  for(const [code,value] of [["en",en],["es",es],["ru",ru],["zh",zh],["pt",pt],["fr",fr]])
    catalogs[code].set(es,value);
}
for(const [es,en] of extraEn){
  const translations=extraLocales.get(es);
  if(!translations || translations.length!==4)
    throw Error("Missing four translations for: "+es);
  for(const [code,value] of [
    ["en",en],["es",es],["ru",translations[0]],
    ["zh",translations[1]],["pt",translations[2]],["fr",translations[3]]]){
    if(!value)throw Error("Empty translation for: "+es+" / "+code);
    catalogs[code].set(es,value);
  }
}
// Permit English-authored HTML and legacy Spanish keys to share one catalog.
const englishSourceKeys = new Map();
for(const [spanish,english] of catalogs.en){
  if(!englishSourceKeys.has(english))englishSourceKeys.set(english,spanish);
}
const originalText = new WeakMap();
const originalAttrs = new WeakMap();
let language="en";
/** Pure audit of completeness across the six local UI catalogs. */
export function translationCoverage(){
  const sourceKeys=[...catalogs.en.keys()];
  return Object.fromEntries(LOCALES.map(({code})=>{
    const missing=sourceKeys.filter(key=>{
      const value=catalogs[code].get(key);
      return typeof value!=="string" || value.trim().length===0;
    });
    return [code,{total:sourceKeys.length,missing}];
  }));
}
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
const dynamicMessages = [
  [new RegExp("^Invalid variable: (.+)$"), ["Invalid variable: {1}","Недопустимая переменная: {1}","变量无效：{1}","Variável inválida: {1}","Variable invalide : {1}"]],
  [new RegExp("^Missing variable: (.+)$"), ["Missing variable: {1}","Отсутствует переменная: {1}","缺少变量：{1}","Variável ausente: {1}","Variable manquante : {1}"]],
  [new RegExp("^Query line (\\d+): use name=value\\.$"), ["Query line {1}: use name=value.","Строка параметров {1}: используйте имя=значение.","查询参数第 {1} 行：请使用 名称=值。","Linha da consulta {1}: use nome=valor.","Ligne de paramètres {1} : utilisez nom=valeur."]],
  [new RegExp("^Invalid query on line (\\d+)$"), ["Invalid query on line {1}.","Недопустимые параметры в строке {1}.","第 {1} 行的查询参数无效。","Consulta inválida na linha {1}.","Paramètres invalides à la ligne {1}."]],
  [new RegExp("^Header line (\\d+): use Name: value\\.$"), ["Header line {1}: use Name: value.","Строка заголовков {1}: используйте Имя: значение.","请求头第 {1} 行：请使用 名称: 值。","Linha de cabeçalho {1}: use Nome: valor.","Ligne d’en-tête {1} : utilisez Nom: valeur."]],
  [new RegExp("^Invalid header: (.+)$"), ["Invalid header: {1}","Недопустимый заголовок: {1}","请求头无效：{1}","Cabeçalho inválido: {1}","En-tête invalide : {1}"]],
  [new RegExp("^(.+) is controlled by the network engine\\.$"), ["{1} is controlled by the network engine.","{1} управляется сетевым движком.","{1} 由网络引擎控制。","{1} é controlado pelo motor de rede.","{1} est géré par le moteur réseau."]],
  [new RegExp("^The browser cannot set header (.+)\\. Use the local gateway for restricted headers\\.$"), ["The browser cannot set header {1}. Use the local gateway for restricted headers.","Браузер не может задать заголовок {1}. Используйте локальный шлюз.","浏览器无法设置请求头 {1}。请使用本地网关处理受限请求头。","O navegador não pode definir {1}. Use o gateway local para cabeçalhos restritos.","Le navigateur ne peut pas définir {1}. Utilisez la passerelle locale."]],
  [new RegExp("^([A-Z-]+) cannot have a body in Reqestra\\.$"), ["{1} cannot have a body in Reqestra.","{1} не поддерживает тело запроса в Reqestra.","Reqestra 中的 {1} 不能包含请求体。","{1} não pode ter corpo no Reqestra.","{1} ne peut pas contenir de corps dans Reqestra."]],
  [new RegExp("^Invalid route: (.+)$"), ["Invalid route: {1}","Недопустимый путь: {1}","路由无效：{1}","Rota inválida: {1}","Chemin invalide : {1}"]],
  [new RegExp("^Missing argument (.+)$"), ["Missing argument {1}","Отсутствует аргумент {1}","缺少参数 {1}","Argumento ausente {1}","Argument manquant {1}"]],
  [new RegExp("^Unsupported cURL option: (.+)$"), ["Unsupported cURL option: {1}","Неподдерживаемый параметр cURL: {1}","不支持的 cURL 选项：{1}","Opção cURL não suportada: {1}","Option cURL non prise en charge : {1}"]],
  [new RegExp("^Browser does not allow session header (.+)$"), ["Browser does not allow session header {1}","Браузер запрещает заголовок сессии {1}","浏览器不允许会话请求头 {1}","O navegador não permite o cabeçalho de sessão {1}","Le navigateur interdit l’en-tête de session {1}"]],
  [new RegExp("^Gateway: respuesta (\\d+)$"), ["Gateway: response {1}","Шлюз: ответ {1}","网关：响应 {1}","Gateway: resposta {1}","Passerelle : réponse {1}"]],
  [new RegExp("^Headers guardados exclusivamente para (.+)$"), ["Origin headers saved for {1}.","Заголовки источника сохранены для {1}.","已保存 {1} 的源站请求头。","Cabeçalhos da origem salvos para {1}.","En-têtes d’origine enregistrés pour {1}."]],
  [new RegExp("^Request completed\\. HTTP (\\d+) · (\\d+) ms$"), ["Request completed. HTTP {1} · {2} ms","Запрос выполнен. HTTP {1} · {2} мс","请求完成。HTTP {1} · {2} 毫秒","Requisição concluída. HTTP {1} · {2} ms","Requête terminée. HTTP {1} · {2} ms"]],
  [new RegExp("^Delete variable (.+)\\?$"), ["Delete variable {1}?","Удалить переменную {1}?","删除变量 {1}？","Excluir variável {1}?","Supprimer la variable {1} ?"]],
  [new RegExp("^Petición guardada en (.+)$"), ["Request saved in {1}.","Запрос сохранён в {1}.","请求已保存至 {1}。","Requisição salva em {1}.","Requête enregistrée dans {1}."]],
  [new RegExp("^Request loaded from (.+)$"), ["Request loaded from {1}.","Запрос загружен из {1}.","已从 {1} 加载请求。","Requisição carregada de {1}.","Requête chargée depuis {1}."]],
  [new RegExp("^Importadas (\\d+) operaciones OpenAPI\\.$"), ["Imported {1} OpenAPI operations.","Импортировано {1} операций OpenAPI.","已导入 {1} 个 OpenAPI 操作。","Importadas {1} operações OpenAPI.","{1} opérations OpenAPI importées."]],
  [new RegExp("^This will send (\\d+) real requests that may modify data\\. Continue\\?$"), ["This will send {1} real requests that may modify data. Continue?","Будет отправлено {1} реальных запросов, которые могут изменить данные. Продолжить?","即将发送 {1} 个可能修改数据的真实请求。继续？","Serão enviadas {1} requisições reais que podem alterar dados. Continuar?","Cela enverra {1} requêtes réelles susceptibles de modifier des données. Continuer ?"]],
  [new RegExp("^Pruebas terminadas: (\\d+) correctas / (\\d+)$"), ["Tests completed: {1} passed / {2} total.","Тесты завершены: {1} успешно из {2}.","测试完成：通过 {1} / 共 {2}。","Testes concluídos: {1} aprovados / {2} no total.","Tests terminés : {1} réussis sur {2}."]],
  [new RegExp("^Se agregaron (\\d+) passive paths for review\\.$"), ["Added {1} passive paths for review.","Добавлено {1} путей для проверки.","已添加 {1} 个被动发现的路径供检查。","Adicionadas {1} rotas passivas para revisão.","{1} chemins passifs ajoutés pour examen."]],
  [new RegExp("^Send (\\d+) requests to the same origin, one per second\\? Mutating methods may change data\\.$"), ["Send {1} requests to the same origin, one per second? Mutating methods may change data.","Отправить {1} запросов на тот же источник, по одному в секунду? Методы изменения могут повлиять на данные.","以每秒一个请求的速度向同源站发送 {1} 个请求？修改类方法可能更改数据。","Enviar {1} requisições à mesma origem, uma por segundo? Métodos de alteração podem modificar dados.","Envoyer {1} requêtes à la même origine, une par seconde ? Les méthodes de modification peuvent changer les données."]],
];
export function t(source,locale=language){
  const text=String(source??"");
  const selected=normalizeLocale(locale);
  const entries=catalogs[selected]||catalogs.en;
  const key=catalogs.en.has(text)?text:(englishSourceKeys.get(text)??text);
  const exact=entries.get(key)??catalogs.en.get(key);
  if(exact!==undefined)return exact;
  if(selected==="es")return text;
  const index={en:0,ru:1,zh:2,pt:3,fr:4}[selected]??0;
  for(const [pattern,forms] of dynamicMessages){
    const match=pattern.exec(text);
    if(!match)continue;
    return forms[index].replace(/\{([1-9])\}/g,(_,num)=>match[Number(num)]??"");
  }
  return text;
}
export function setLocale(next,{persist=true}={}){
  language=normalizeLocale(next);
  document.documentElement.lang=language==="zh"?"zh-Hans":language;
  const description=document.querySelector('meta[name="description"]');
  if(description)description.content=t(
    "Reqestra Studio: cliente HTTP web para inspección, colecciones, variables, pruebas y exploración autorizada."
  );
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
  if(!node.parentElement || node.parentElement.closest("[data-no-translate],pre,code,script,style,textarea,option[value=GET],option[value=POST]"))return;
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
  const trigger=document.getElementById("languageTrigger");
  const menu=document.getElementById("languageMenu");
  const current=document.getElementById("languageCurrent");
  const control=document.getElementById("languageControl");
  if(!select||!trigger||!menu||!current||!control)
    throw Error("Language controls are missing.");

  select.replaceChildren();
  for(const entry of LOCALES){
    const option=document.createElement("option");
    option.value=entry.code;option.textContent=entry.name;select.append(option);
  }
  const stored=localStorage.getItem("reqestra.locale");
  language=stored&&LOCALES.some(l=>l.code===stored)?stored:deviceLocale();
  select.value=language;

  function refreshMenu(){
    const chosen=LOCALES.find(x=>x.code===language)||LOCALES[0];
    current.textContent=chosen.name;
    trigger.setAttribute("aria-label",t("Idioma de la interfaz")+": "+chosen.name);
    trigger.title=t("Idioma de la interfaz")+": "+chosen.name;
    menu.setAttribute("aria-label",t("Idioma de la interfaz"));
    menu.replaceChildren();
    const caption=document.createElement("div");
    caption.className="language-menu-heading";
    caption.textContent=t("Idioma de la interfaz");
    menu.append(caption);

    for(const entry of LOCALES){
      const selected=language===entry.code;
      const button=document.createElement("button");
      button.type="button";
      button.className="language-option";
      button.setAttribute("role","option");
      button.setAttribute("aria-selected",String(selected));
      button.setAttribute("aria-label",entry.name+(selected?" · "+t("Seleccionado"):""));
      button.dataset.locale=entry.code;

      const label=document.createElement("span");
      const code=document.createElement("span");
      code.className="language-option-code";
      code.textContent=entry.code.toUpperCase();
      label.append(code,document.createTextNode(" "+entry.name));
      button.append(label);
      if(selected){
        const check=document.createElement("span");
        check.className="language-option-check";check.textContent="✓";
        check.setAttribute("aria-hidden","true");button.append(check);
      }
      button.addEventListener("click",()=>{
        select.value=entry.code;
        setLocale(entry.code);
        refreshMenu();
        closeMenu();
        trigger.focus();
        onChange(language);
      });
      menu.append(button);
    }
  }
  function closeMenu(){
    menu.hidden=true;
    trigger.setAttribute("aria-expanded","false");
  }
  function openMenu(){
    refreshMenu();
    menu.hidden=false;
    trigger.setAttribute("aria-expanded","true");
    menu.querySelector('[aria-selected="true"]')?.focus();
  }
  trigger.addEventListener("click",()=>{
    if(menu.hidden)openMenu();else closeMenu();
  });
  trigger.addEventListener("keydown",event=>{
    if(event.key==="ArrowDown"||event.key==="Enter"||event.key===" "){
      event.preventDefault();openMenu();
    }
  });
  menu.addEventListener("keydown",event=>{
    if(event.key==="Escape"){
      event.preventDefault();closeMenu();trigger.focus();return;
    }
    if(event.key==="ArrowDown"||event.key==="ArrowUp"){
      event.preventDefault();
      const choices=[...menu.querySelectorAll(".language-option")];
      const pos=choices.indexOf(document.activeElement);
      choices[(pos+(event.key==="ArrowDown"?1:-1)+choices.length)%choices.length]?.focus();
    }
  });
  document.addEventListener("pointerdown",event=>{
    if(!control.contains(event.target))closeMenu();
  });
  document.addEventListener("keydown",event=>{
    if(event.key==="Escape"&&!menu.hidden){closeMenu();trigger.focus();}
  });
  select.addEventListener("change",()=>{
    setLocale(select.value);
    refreshMenu();
    onChange(language);
  });
  setLocale(language,{persist:false});
  refreshMenu();

  let pending=false;
  // Localized text inserted by the app is translated without remote calls.
  const observer=new MutationObserver(()=>{
    if(pending)return;
    pending=true;
    queueMicrotask(()=>{pending=false;translateDom();});
  });
  observer.observe(document.body,{subtree:true,childList:true,characterData:true});
  return language;
}
