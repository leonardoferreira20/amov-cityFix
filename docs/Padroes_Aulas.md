# Padrões dados nas aulas

Resumo do que foi ensinado nas aulas práticas, para o CityFix seguir os mesmos padrões.
Fonte principal: o projeto ContactApp e a ficha [AMov.P07.pdf](AMov.P07.pdf). O código das aulas não está neste repositório.

Se uma solução precisar de algo que não está aqui, avisar antes de o propor.

## Estado das aulas

| Aula | Tema | Estado |
|---|---|---|
| #1 | Modelo de domínio, MVVM com `StateFlow`, navegação type-safe | Dada |
| #2 | Formulários com `UiState` e validação, `DatePicker`, repositório com JSON | Dada |
| #3 | Multimédia: Photo Picker, câmara com `FileProvider` e `TakePicture`, imagens com Coil 3 | Próxima |
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
- A coleção é uma classe própria (como `ContactsList`), com `findById`, `add`, `upsert`, `rawList` e ordenação com `java.text.Collator` para os acentos.

## ViewModel

- Estado privado em `MutableStateFlow`, exposto como `StateFlow` com `asStateFlow()`.
- Alterações com `_state.update { it.copy(...) }`.
- Trabalho assíncrono com `viewModelScope.launch`.
- Um método por evento do ecrã, por exemplo `onNameChanged(newName: String)`.

## UiState para formulários

- `data class` com os valores dos campos, um erro por campo (`nameError: String?`), `isSaving` e uma propriedade calculada `isValid`.
- A validação é feita no ViewModel a cada alteração, com expressões regulares para o e-mail e o telefone.
- Um único ecrã serve para criar e editar: a rota recebe `id: String? = null`, que é `null` na criação.

## Repositório

- Classe concreta em `data/`, sem interface.
- Recebe `Context`, tem dados de exemplo e grava em JSON no armazenamento interno (`context.filesDir`) com `kotlinx.serialization.json`.
- As operações de ficheiro correm em `withContext(Dispatchers.IO)`.
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

## Previews

- `@Preview(showBackground = true)` numa função `private`, com o tema da app e dados de exemplo.

## Diferença em relação às aulas

- Nas aulas há textos fixos no código, como `"No contacts registered."`. No CityFix os textos vão sempre para `strings.xml`, porque o enunciado exige inglês e português.
