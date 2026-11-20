# Padrões dados nas aulas

Resumo do que foi ensinado nas aulas práticas, para o CityFix seguir os mesmos padrões.
Fonte principal: o projeto ContactsApp (versão de 6/11 e `Contacts_#3`, com GPS e mapas) e as fichas AMov.P07 a AMov.P10 ([AMov.P07.pdf](AMov.P07.pdf); as P08, P09 e P10 estão com os alunos). Também o PaintPrototype e a ficha AMov.P01 (introdução ao Kotlin). O código das aulas não está neste repositório.

Se uma solução precisar de algo que não está aqui, avisar antes de o propor.

## Estado das aulas

| Aula | Tema | Estado |
|---|---|---|
| #1 | Modelo de domínio, MVVM com `StateFlow`, navegação type-safe | Dada |
| #2 | Formulários com `UiState` e validação, `DatePicker`, repositório com JSON | Dada |
| #3 | Multimédia: Photo Picker, câmara com `FileProvider` e `TakePicture`, imagens com Coil 3 | Dada |
| #4 | GPS com `FusedLocationProvider`, permissões em runtime, Maps Compose e OSM, serviço em primeiro plano com proximidade, layouts adaptativos (ficha P10) | Dada; aplicada a 16/11 |
| — | Firebase, WorkManager, sensores | Sem ficha; segue-se a documentação oficial (decidido a 16/11) |

Também foram dados, em projetos com Views: recursos por língua (`values-pt`), `layout-land`, menus e notificações (`NotificationChannel`, `NotificationCompat`, permissão `POST_NOTIFICATIONS`).

## Configuração do Firebase

- Configuração inicial baseada na [documentação do Firebase para Android](https://firebase.google.com/docs/android/setup). O plugin Google Services gera os recursos da aplicação a partir de `app/google-services.json`. O catálogo do Gradle define a versão do Firebase BoM, que gere a versão do SDK de Authentication.
- O BoM 34.19.0 é usado para manter a compatibilidade com o Kotlin 2.2.10 do projeto. O `google-services.json` do projeto `cityfix-3dbb1` está no repositório e é partilhado pelos dois.

## Pacotes

| Pacote | Conteúdo |
|---|---|
| `model` | Classes de dados em Kotlin puro, sem imports de Android |
| `viewmodel` | ViewModels e classes `UiState` |
| `data` | Repositórios |
| `utils` | Funções e classes de apoio |
| `ui.navigation` | Rotas type-safe (`Destinations.kt`) |
| `ui.components` | Composables reutilizáveis e stateless |
| `ui.screens` | Ecrãs |
| `ui.theme` | Cores, tema, tipografia |

## Modelo

- `data class` com `@Serializable`, `id` com `UUID.randomUUID().toString()` por omissão e campos opcionais com `= null`.
- A coleção é uma classe própria (como `ContactsList`), com `findById`, `upsert`, `rawList` e ordenação com `java.text.Collator` para os acentos.

## ViewModel

- Estado privado em `MutableStateFlow`, exposto como `StateFlow` com `asStateFlow()`.
- Alterações com `_state.update { it.copy(...) }`.
- Trabalho assíncrono com `viewModelScope.launch`.
- Um método por evento do ecrã, por exemplo `onNameChanged(newName: String)`.
- Variante do PaintPrototype: a classe de modelo tem o seu próprio `StateFlow`, e o ViewModel recolhe-o em `viewModelScope` e converte-o num tipo próprio da UI. Esse ViewModel não recebe dependências, por isso é criado com `viewModel()`, sem Factory.

## UiState para formulários

- `data class` com os valores dos campos, um erro por campo (`nameError: String?`), `isSaving` e uma propriedade calculada `isValid`.
- A validação é feita no ViewModel a cada alteração. O e-mail é validado com uma expressão regular. O telefone só é validado pelo comprimento (pelo menos 9 caracteres).
- Um único ecrã serve para criar e editar: a rota recebe `id: String? = null`, que é `null` na criação.

## Repositório

- Classe concreta em `data/`, sem interface.
- Recebe `Context`, tem dados de exemplo e grava em JSON no armazenamento interno (`context.filesDir`) com `kotlinx.serialization.json`.
- As operações de ficheiro correm em `withContext(Dispatchers.IO)`.
- Guarda a lista em cache depois da primeira leitura. `getXById` lê dessa cache.
- É criado uma vez numa classe `Application` (`val repository by lazy { ... }`) e passado aos ViewModels através de uma `ViewModelProvider.Factory`.
- Na `MainActivity`: `viewModel(factory = XViewModelFactory(app.repository))`. Para um ViewModel que só vive num ecrã: `viewModel(viewModelStoreOwner = backStack, factory = ...)`.

## Navegação

- Todas as rotas num ficheiro `Destinations.kt` em `ui.navigation`, cada uma com `@Serializable`.
- `data object` para rotas sem parâmetros e `data class` para rotas com parâmetros.
- `NavHost` na `MainActivity`, dentro de um `Scaffold`, com `Modifier.padding(innerPadding)`.
- `composable<Route> { backStack -> backStack.toRoute<Route>() }` para ler os parâmetros.
- `navController.navigate(...)`, `popBackStack()` depois de gravar, `navigateUp()` na seta de voltar.
- Obriga ao plugin `kotlin-serialization`.

## Barra de topo contextual

- `MainTopAppBar` recebe o `navController` e os callbacks.
- Sabe o ecrã atual com `navController.currentBackStackEntryAsState()` e `destination?.hasRoute<Route>()`.
- Mostra a seta de voltar em todos os ecrãs exceto o principal, e as ações só nos ecrãs onde fazem sentido.
- `TopAppBar` precisa de `@OptIn(ExperimentalMaterial3Api::class)`.

## Ecrãs stateful e stateless

- Stateful: `XScreen(viewModel: XViewModel = viewModel())`, recolhe o estado com `collectAsStateWithLifecycle()` e chama o stateless.
- Stateless: `XScreenContent(dados, callbacks)`, recebe dados imutáveis e devolve eventos por lambdas. Não conhece o ViewModel.

## Listas e cartões

- `LazyColumn` com `contentPadding = PaddingValues(16.dp)`, `verticalArrangement = Arrangement.spacedBy(8.dp)` e `items(..., key = { it.id })`.
- Lista vazia: `Box` centrado com uma mensagem.
- `Card` com `.fillMaxWidth().clickable(onClick = onClick)` e elevação de 2 dp.
- Cartão expansível com `AnimatedVisibility(visible = isExpanded)`. O `isExpanded` fica na `MainActivity` com `rememberSaveable` e muda com um botão na barra de topo.

## Multimédia

- Galeria: `rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())`, lançado com `PickVisualMediaRequest(PickVisualMedia.ImageOnly)`. O `Uri` devolvido é copiado para `filesDir/images/` e guarda-se o caminho absoluto.
- Câmara: `ActivityResultContracts.TakePicture()`. Cria-se primeiro um ficheiro temporário em `filesDir/images/` e passa-se o seu `Uri` do `FileProvider`. O caminho só é guardado se `success` for verdadeiro.
- `FileProvider` declarado no manifesto com `authorities="${applicationId}.fileprovider"`, `exported="false"` e `grantUriPermissions="true"`, e `res/xml/file_paths.xml` com `<files-path path="images/" />`.
- `utils/FileUtils` (um `object`) com `copyUriToInternalStorage`, `createTempImageFile` e `deleteFileFromPath`.
- Ao gravar, se a imagem mudou, o ViewModel apaga o ficheiro antigo.
- Os launchers ficam no composable do ecrã, que obtém o `Context` com `LocalContext.current`. O ViewModel só recebe o caminho, através de um callback (`onPictureChange(String?)`).
- Imagens mostradas com `AsyncImage` do Coil 3 (`coil-compose` e `coil-network-okhttp`). Sem imagem, mostra as iniciais num círculo (`ContactAvatar`).

## Ecrãs com FAB e diálogos

- O ecrã de edição e o de detalhe têm o seu próprio `Scaffold` com `FloatingActionButton`: ✓ para gravar e ✏️ para editar.
- O formulário é uma `Column` com `verticalScroll` e `imePadding()`.
- Datas com `DatePickerDialog` e `rememberDatePickerState`. A visibilidade do diálogo fica num `remember { mutableStateOf(false) }` local.

## Localização e mapas (aula #4, ficha P10)

- Dependências `maps-compose` 6.1.0 e `play-services-location` 21.3.0, no catálogo do Gradle.
- Manifesto com `ACCESS_FINE_LOCATION` e `ACCESS_COARSE_LOCATION`, e a chave em `<meta-data android:name="com.google.android.geo.API_KEY">`.
- `utils/LocationUtils` (um `object`): `hasLocationPermission` com `ContextCompat.checkSelfPermission`; `locationUpdates` com `callbackFlow`, `LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalo)`, `lastLocation` e `requestLocationUpdates`, e `removeLocationUpdates` no `awaitClose`; distâncias com `Location.distanceBetween`.
- A posição do dispositivo chega ao composable com `produceState` (como o `ContactDistanceSection`).
- Permissões pedidas na `MainActivity` com `rememberLauncherForActivityResult(RequestMultiplePermissions())`, lançado num `LaunchedEffect(Unit)`.
- Mapa: `GoogleMap` com `rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(...) }` e `Marker(state = remember(pin) { MarkerState(pin) })`.
- Escolha de um ponto: `MapLocationPicker` com `onMapClick`.
- Landscape: `LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE`; o conteúdo vai para lambdas `@Composable (Modifier) -> Unit` que se põem numa `Row` com `weight(1f)` em landscape e numa `Column` em portrait.
- Serviço em primeiro plano `ProximityService` (`foregroundServiceType="location"`, permissões `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION` e `POST_NOTIFICATIONS`): canal de notificações, `startForeground`, `LocationCallback` com `requestLocationUpdates`, um `CoroutineScope(Dispatchers.IO + SupervisorJob())` e um conjunto de ids já avisados para não repetir; `removeLocationUpdates` e `cancel` no `onDestroy`. Arrancado na `MainActivity` com `startForegroundService` depois da permissão.
- Mapa com todos os contactos (`ContactsMapScreen`): câmara ajustada a todos os pinos com `LatLngBounds` e `CameraUpdateFactory.newLatLngBounds`.
- Distância e navegação (`ContactDistanceSection`): `AssistChip` com a distância e botão que abre o trajeto com um `Intent` `geo:` (com o endereço web do Google Maps como alternativa).
- Também dados mas não usados: geocoder (`LocationUtils.reverseGeocode`), pinos com avatar (`MapUtils`) e osmdroid.

## Dependências usadas nas aulas

- `kotlinx-serialization-json` 1.11.0 (repositório em JSON).
- `coil-compose` e `coil-network-okhttp` 3.0.4 (imagens).
- `navigation-compose` 2.8.5 e `material-icons-extended`.
- `maps-compose` 6.1.0 e `play-services-location` 21.3.0 (aula #4).

## Previews

- `@Preview(showBackground = true)` numa função `private`, com o tema da app e dados de exemplo.

## Diferença em relação às aulas

- Nas aulas há textos fixos no código, como `"No contacts registered."`. No CityFix os textos vão sempre para `strings.xml`, porque o enunciado exige inglês e português.
- Por isso, os erros de validação no `UiState` guardam o id da string (`@StringRes val nameError: Int?`) em vez do texto. O ViewModel não tem `Context` para ler a string; é o ecrã que a converte com `stringResource`.
- O ViewModel de edição carrega o registo com `loadX()` em vez de ler só a cache do repositório (`getXById`). Assim funciona mesmo que o ecrã de edição seja o primeiro a abrir, por exemplo depois de o Android terminar a app em segundo plano.
- O `NavHost` usa `Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)`. Não foi dado nas aulas: sem o `consumeWindowInsets`, os ecrãs que têm o seu próprio `Scaffold` (categorias, registo, validação) voltavam a somar o espaço da barra de estado e ficavam com uma faixa em branco no topo.
- O seletor de perfil usa `FilterChip` e `horizontalScroll(rememberScrollState())`. Também não foram dados: o `FilterChip` é o componente do Material 3 para escolher uma opção entre várias, e o scroll horizontal segue a mesma ideia do `verticalScroll` do `EditScreen`.
- A `MainTopAppBar` decide o título com `hasRoute<...>()` sobre a entrada atual, tal como as aulas fazem para saber se é o ecrã principal, mas para várias rotas.
- O Contacts alterna a expansão de todos os cartões com um booleano na barra de topo e abre o detalhe ao tocar no cartão. O CityFix usa o mesmo `AnimatedVisibility` e `rememberSaveable`, guardando os IDs expandidos para o toque no cartão e para o botão global. Um `TextButton` dentro da expansão abre o detalhe.
- O CityFix configura `SystemBarStyle.dark(Color.TRANSPARENT)` no `enableEdgeToEdge()`, um parâmetro que não aparece nos exemplos das aulas. Mantém os ícones do Android claros sobre a barra de topo azul-petróleo.
- O código de galeria e câmara do `EditScreen` das aulas foi reunido num componente `PhotoPicker`, para não se repetir na criação, na reabertura e nos ecrãs do Leonardo. Dentro de um `AlertDialog` funciona igual, porque o diálogo herda o registo de resultados da Activity.
- Na criação de ocorrências, o caminho do ficheiro temporário da câmara fica num `rememberSaveable` (uma `String`) em vez de `remember`. Nas aulas, se o telemóvel rodasse com a câmara aberta, a Activity era recriada e a foto perdia-se; o enunciado exige landscape.
- Se o utilizador cancelar a câmara, o ficheiro temporário é apagado (`FileUtils.deleteFromPath`), para não ficarem ficheiros vazios em `filesDir/images/`.
- Usa-se também o `coil-network-okhttp`, como nas aulas, porque as fotos enviadas para o Storage (L9) são mostradas a partir da internet.
- A visibilidade usa o mesmo padrão de `RadioButton` com `selectable` e `selectableGroup` do registo do Leonardo, numa função genérica `RadioOptions<T>`.
- A categoria e a situação usam uma lista dropdown do Material 3 (`ExposedDropdownMenuBox`, com `OutlinedTextField` só de leitura e `DropdownMenuItem`), numa função genérica `DropdownField<T>`. Não foi dada nas aulas. Escolheu-se para o formulário caber no ecrã. Precisa de `@OptIn(ExperimentalMaterial3Api::class)`, como a `TopAppBar` das aulas.
- O `UserAvatar` segue o `ContactAvatar` das aulas: um círculo com `AsyncImage` e as iniciais do nome quando não há foto. Sem nome, mostra o ícone de pessoa em vez de "?", para não haver texto fixo. A descrição de acessibilidade fica num só nó com `clearAndSetSemantics`, que não foi dado nas aulas: assim o leitor de ecrã lê "Fotografia de perfil" em vez das iniciais soltas.
- Os botões Galeria / Câmara passaram para um componente próprio, `PhotoSourceButtons`, que o `PhotoPicker` usa por dentro. O parâmetro `vertical` põe-nos um por baixo do outro, ao lado do avatar, como no `EditScreen` das aulas.
- O `RegisterViewModel` apaga a foto escolhida em `onCleared()` se o registo não chegar a ser feito. O `onCleared()` não foi dado nas aulas: o Android chama-o quando o ViewModel deixa de ser preciso, aqui ao sair do ecrã de registo.
- No perfil, os dados da conta só de leitura são rótulos e valores com `Text`, separados do formulário editável. O início usa `Box` para as barras dos gráficos e `LazyListState.animateScrollToItem` para ir à lista. O atalho com `LazyListState` não foi dado nas aulas.
- O registo e o perfil partilham os campos do formulário (`FormTextField`, `UserPhotoField` e `ImpactDetectionSwitch`, em `ui/components/UserFormFields.kt`), em vez de cada ecrã ter a sua cópia.
- A troca de foto no perfil segue o `initialPicturePath` do `EditContactViewModel` das aulas: a foto anterior só é apagada ao gravar. O CityFix acrescenta duas coisas: uma foto escolhida e logo trocada por outra é apagada na troca, e uma foto nova que não chegou a ser gravada é apagada em `onCleared()`.
- No `EditContactViewModel` das aulas, nada impede um segundo toque no botão de gravar enquanto a gravação decorre. No CityFix isso viu-se no emulador: dois toques seguidos no ✓ das categorias gravavam a categoria duas vezes e faziam voltar dois ecrãs atrás. Por isso, o `EditCategoryViewModel` e o `ProfileViewModel` ignoram os toques seguintes enquanto o `isSaving` está a `true`. Também não o voltam a pôr a `false` depois de gravar, porque o ecrã fecha a seguir: assim, um toque durante a animação de saída não grava outra vez nem faz voltar atrás duas vezes.
- A chave do Google Maps está escrita no `AndroidManifest.xml`, como nas aulas, e é a mesma para os dois alunos.
- O `LocationUtils` do CityFix emite `GeoLocation` (a classe do modelo) em vez de `android.location.Location`, para o ViewModel não depender de classes do Android. Não tem geocoder, porque só se guardam coordenadas.
- O mapa do detalhe público desliga os gestos de zoom e deslocação com `MapUiSettings`, que não foi dado nas aulas: sem isso, o zoom afastado não esconderia o local exato.
- No mapa de ocorrências, o ajuste aos pinos usa `onSizeChanged` e a versão de `newLatLngBounds` que recebe largura e altura em píxeis. O efeito está dentro do conteúdo do `GoogleMap`, que é composto depois de a instância do mapa estar disponível.
- Os mapas mostram uma caixa cinzenta nos previews (`LocalInspectionMode`, não dado nas aulas), porque o `GoogleMap` não desenha no preview do Android Studio.
- Nas aulas, a câmara com `TakePicture` não pede a permissão `CAMERA`. O enunciado exige pedir a permissão da câmara em runtime, por isso o CityFix declara-a no manifesto e o botão Câmara pede-a com `RequestPermission()` antes de abrir a câmara. Com a permissão declarada, o Android recusa o `TakePicture` sem ela.
- O `ReportIncidentScreen` reutiliza o `MapLocationPicker` do registo do munícipe, já baseado na P10. O toque no mapa envia um `GeoLocation` para `ReportIncidentViewModel.onLocationSelected` e a foto tem o seu próprio evento.
- As datas formatam-se em `utils/formatDateTime` e não dentro dos composables, porque o lint recusa `Locale.getDefault()` em composables (devia usar o `Locale` da configuração).
- Ao gravar, os ViewModels copiam a lista (`IncidentsList(repository.loadIncidents().rawList())`) antes de a alterar. Se a gravação falhar (`IOException`), a cache do repositório fica como estava e o ecrã mostra um erro. Nas aulas a lista em cache é alterada diretamente.
- O `ProximityService` do CityFix usa `ServiceCompat.startForeground(..., FOREGROUND_SERVICE_TYPE_LOCATION)` em vez de `startForeground(id, notificação)`, para indicar o tipo do serviço a partir do Android 10; recebe o id do operador por um extra do `Intent` (`onStartCommand`). Em vez de mostrar uma notificação a cada ocorrência próxima, como o Contactos, grava o alerta na ocorrência, porque quem tem de ser avisado é o autor, noutro telemóvel.
- O serviço é arrancado e parado num `LaunchedEffect(currentUser?.id, hasLocationPermission)`, para correr só com a sessão de operador. Nas aulas arranca uma vez, no resultado do pedido de permissões.
- A localização em 2.º plano (`ACCESS_BACKGROUND_LOCATION`) não foi dada nas aulas. O Android obriga a pedi-la depois da de 1.º plano e abre as definições, por isso o CityFix mostra antes um `AlertDialog` a explicar e só a pede ao operador e ao munícipe com a deteção de impactos.
- O pedido inicial de permissões é lançado se faltar a localização ou as notificações (nas aulas é lançado sempre).
- As listas de categorias e de munícipes pendentes usam `LazyVerticalGrid` com `GridCells.Adaptive(320.dp)`: o número de colunas depende da largura disponível, por isso fica uma coluna em portrait e duas em landscape sem verificar a orientação. Não foi dado nas aulas.
- Acessibilidade sem ficha das aulas: os títulos de secção têm `semantics { heading() }`, para o leitor de ecrã poder saltar entre secções; os dados da conta usam `semantics(mergeDescendants = true)`, para o rótulo e o valor serem lidos juntos; e os erros do registo e do login têm `liveRegion`, para serem anunciados quando aparecem.
- Os previews em landscape usam `device = "spec:width=411dp,height=891dp,orientation=landscape"`, para o `LocalConfiguration` dar a orientação certa.

## Firebase (sem ficha das aulas, segundo a documentação oficial)

- Dependências pelo Firebase BoM 34 (`firebase-auth`, `firebase-firestore`), sem os módulos `-ktx`, que saíram do BoM 34; as extensões Kotlin importam-se de `com.google.firebase.Firebase`, `com.google.firebase.auth.auth` e `com.google.firebase.firestore.firestore`.
- `kotlinx-coroutines-play-services` para usar `task.await()` dentro de `viewModelScope.launch`.
- Os repositórios continuam a ser classes concretas em `data/`, criadas na `CityFixApp` e passadas por `Factory`, como nas aulas; só muda o armazenamento (Firestore em vez de JSON). Por isso deixaram de receber `Context`.
- `toObject()` / `toObjects()` exigem um construtor sem argumentos: todos os campos dos modelos têm valor por omissão.
- Leituras com `get().await()`; gravações com `set()` sem `await`, porque o `await` só termina quando o servidor responde e a app tem de funcionar offline. A exceção é a criação do utilizador no registo, que espera, porque a sessão termina logo a seguir.
- A lista do início usa `addSnapshotListener` dentro de um `callbackFlow`, como o `locationUpdates` da P10.
- MFA com `TotpMultiFactorGenerator`: no perfil, `multiFactor.session` → `generateSecret` → `getAssertionForEnrollment` → `enroll`; no login, `FirebaseAuthMultiFactorException` → `resolver` → `getAssertionForSignIn` → `resolveSignIn`.
- Os erros do Firebase convertem-se em ids de `strings.xml` numa função (`utils/authErrorMessage`), como os erros de validação.
- Storage (L9, 17/11): `Firebase.storage.reference.child("incidents/<id>/<foto>.jpg")`, `putFile(...).await()` e `downloadUrl.await()`; o tipo `image/jpeg` vai no `storageMetadata`. O projeto usa o modo de teste do Storage, por indicação do professor; o `storage.rules` fica como versão de produção.
- WorkManager (L9, 17/11): um `CoroutineWorker` (`MediaUploadWorker`) por foto, agendado com `enqueueUniqueWork(..., ExistingWorkPolicy.KEEP, ...)` e a restrição `NetworkType.CONNECTED`, para só correr com rede. Num erro devolve `Result.retry()`, até 5 vezes. O worker obtém o repositório pela `CityFixApp`, porque o WorkManager cria os workers sem `Factory`.
- A compressão usa o `BitmapFactory` com `inSampleSize`, uma `Matrix` para reduzir e rodar segundo o EXIF (`android.media.ExifInterface`) e `Bitmap.compress(JPEG, ...)`.
- No fim do envio, a ocorrência é atualizada com `runTransaction`, para não apagar alterações feitas entretanto noutro telemóvel.
- Cloud Messaging (L11, 17/11): o token (`Firebase.messaging.token`) fica em `deviceTokens/<uid>`, os operadores subscrevem o tópico `operators` e o token é apagado ao terminar sessão (`deleteToken`). O `CityFixMessagingService`, um `FirebaseMessagingService` declarado no manifesto, recebe mensagens só com dados e cria a notificação com os textos do `strings.xml`, para ficar em inglês ou português conforme o telemóvel. O `NotificationChannel` e o `NotificationCompat` foram dados nas aulas, em projetos com Views.
- Cloud Functions (L11, 17/11): JavaScript em `functions/index.js`, com as funções de 2.ª geração `onDocumentUpdated` e `onSchedule` e o `firebase-admin` para ler o Firestore e enviar as mensagens. Publicam-se com o Firebase CLI (`firebase deploy --only functions`), que precisa de Node.js 22 e do plano Blaze. Cada função fica numa região que suporte o seu gatilho: a do Firestore na região da base de dados e a agendada numa região com Cloud Scheduler.
- Tocar numa notificação abre a `MainActivity` com o id da ocorrência num extra do `Intent`; a `MainActivity` navega para o detalhe quando a sessão já está carregada.

## Sensores (sem ficha das aulas, segundo a documentação oficial)

- Deteção de impactos (J10, 20/11): o `ImpactDetectionService` é um serviço em primeiro plano, como o serviço de proximidade da P10. Regista um `SensorEventListener` no `SensorManager` para o `Sensor.TYPE_ACCELEROMETER`, com `SENSOR_DELAY_GAME` (cerca de 50 leituras por segundo), e pede a localização ao `FusedLocationProviderClient` de 5 em 5 s, para saber a velocidade e o ponto do impacto.
- O `ImpactDetector` segue o exemplo da documentação oficial dos sensores de movimento: um filtro passa-baixo (`alpha = 0.8`) estima a gravidade e o que sobra é a aceleração do movimento. A componente vertical é o produto escalar dessa aceleração com a direção da gravidade, por isso não depende da posição do telemóvel. A gravidade começa com a primeira leitura, para não dar um falso impacto ao arrancar.
- O `ImpactDetector` é Kotlin puro e tem testes unitários com JUnit (`ImpactDetectorTest`), que correm sem emulador.
- A sugestão é uma notificação com a latitude e a longitude do impacto em extras do `Intent`; a `MainActivity` navega para `ReportIncidentRoute(impactLatitude, impactLongitude)`, uma rota com argumentos opcionais, como a `EditCategoryRoute`.

## Vídeo (sem ficha das aulas, segundo a documentação oficial)

- O `VideoPicker` (20/11) segue o padrão do `PhotoPicker` da aula #3: grava com o contrato `CaptureVideo`, o equivalente ao `TakePicture`, para um ficheiro do `FileProvider` (novo caminho `videos/`), e escolhe da galeria com o `PickVisualMedia` só com vídeos (`VideoOnly`). A câmara recebe o limite de 30 s e 50 MB pelos extras `MediaStore.EXTRA_DURATION_LIMIT` e `EXTRA_SIZE_LIMIT`, numa subclasse do `CaptureVideo`.
- Antes de copiar um vídeo da galeria, o tamanho é lido com `OpenableColumns.SIZE`; a extensão do ficheiro copiado vem do tipo MIME, com o `MimeTypeMap`.
- O `IncidentVideo` mostra o vídeo com o `VideoView` do Android dentro de um `AndroidView` (interoperabilidade com Views), com um botão de reproduzir e pausar em Compose. O `setVideoPath` aceita tanto o caminho local como o endereço do Storage.
- O `MediaUploadWorker` passou a receber o tipo do ficheiro: as fotos são comprimidas e têm thumbnail; os vídeos são enviados como estão, com o tipo MIME da extensão.
