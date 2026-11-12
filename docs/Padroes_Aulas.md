# Padrões dados nas aulas

Resumo do que foi ensinado nas aulas práticas, para o CityFix seguir os mesmos padrões.
Fonte principal: o projeto ContactsApp (versão de 6/11) e a ficha [AMov.P07.pdf](AMov.P07.pdf). Também o PaintPrototype e a ficha AMov.P01 (introdução ao Kotlin). O código das aulas não está neste repositório.

Se uma solução precisar de algo que não está aqui, avisar antes de o propor.

## Estado das aulas

| Aula | Tema | Estado |
|---|---|---|
| #1 | Modelo de domínio, MVVM com `StateFlow`, navegação type-safe | Dada |
| #2 | Formulários com `UiState` e validação, `DatePicker`, repositório com JSON | Dada |
| #3 | Multimédia: Photo Picker, câmara com `FileProvider` e `TakePicture`, imagens com Coil 3 | Dada |
| #4 | GPS com `FusedLocationProvider`, permissões em runtime, Maps Compose e OSM, layouts adaptativos | Por dar |
| — | Firebase, WorkManager, sensores, geofencing | Ainda sem data |

Também foram dados, em projetos com Views: recursos por língua (`values-pt`), `layout-land`, menus e notificações (`NotificationChannel`, `NotificationCompat`, permissão `POST_NOTIFICATIONS`).

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

## Dependências usadas nas aulas

- `kotlinx-serialization-json` 1.11.0 (repositório em JSON).
- `coil-compose` e `coil-network-okhttp` 3.0.4 (imagens).
- `navigation-compose` 2.8.5 e `material-icons-extended`.

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
- Só se usa `coil-compose`. As aulas incluem também `coil-network-okhttp`, que só é preciso para imagens da internet; as fotos do CityFix são ficheiros locais.
- A visibilidade usa o mesmo padrão de `RadioButton` com `selectable` e `selectableGroup` do registo do Leonardo, numa função genérica `RadioOptions<T>`.
- A categoria e a situação usam uma lista dropdown do Material 3 (`ExposedDropdownMenuBox`, com `OutlinedTextField` só de leitura e `DropdownMenuItem`), numa função genérica `DropdownField<T>`. Não foi dada nas aulas. Escolheu-se para o formulário caber no ecrã. Precisa de `@OptIn(ExperimentalMaterial3Api::class)`, como a `TopAppBar` das aulas.
- O `UserAvatar` segue o `ContactAvatar` das aulas: um círculo com `AsyncImage` e as iniciais do nome quando não há foto. Sem nome, mostra o ícone de pessoa em vez de "?", para não haver texto fixo. A descrição de acessibilidade fica num só nó com `clearAndSetSemantics`, que não foi dado nas aulas: assim o leitor de ecrã lê "Fotografia de perfil" em vez das iniciais soltas.
- Os botões Galeria / Câmara passaram para um componente próprio, `PhotoSourceButtons`, que o `PhotoPicker` usa por dentro. O parâmetro `vertical` põe-nos um por baixo do outro, ao lado do avatar, como no `EditScreen` das aulas.
- O `RegisterViewModel` apaga a foto escolhida em `onCleared()` se o registo não chegar a ser feito. O `onCleared()` não foi dado nas aulas: o Android chama-o quando o ViewModel deixa de ser preciso, aqui ao sair do ecrã de registo.
- No perfil, os campos que não se podem alterar são `OutlinedTextField` com `enabled = false`, um parâmetro que não foi usado nas aulas: aparecem esbatidos, não recebem foco e o leitor de ecrã anuncia-os como desativados.
- O registo e o perfil partilham os campos do formulário (`FormTextField`, `UserPhotoField` e `ImpactDetectionSwitch`, em `ui/components/UserFormFields.kt`), em vez de cada ecrã ter a sua cópia.
- A troca de foto no perfil segue o `initialPicturePath` do `EditContactViewModel` das aulas: a foto anterior só é apagada ao gravar. O CityFix acrescenta duas coisas: uma foto escolhida e logo trocada por outra é apagada na troca, e uma foto nova que não chegou a ser gravada é apagada em `onCleared()`.
- No `EditContactViewModel` das aulas, nada impede um segundo toque no botão de gravar enquanto a gravação decorre. No CityFix isso viu-se no emulador: dois toques seguidos no ✓ das categorias gravavam a categoria duas vezes e faziam voltar dois ecrãs atrás. Por isso, o `EditCategoryViewModel` e o `ProfileViewModel` ignoram os toques seguintes enquanto o `isSaving` está a `true`. Também não o voltam a pôr a `false` depois de gravar, porque o ecrã fecha a seguir: assim, um toque durante a animação de saída não grava outra vez nem faz voltar atrás duas vezes.
