# CityFix — Plano de trabalho

Plano geral do projeto e divisão de tarefas entre João Carvalho e Leonardo Ferreira.
As tarefas detalhadas estão em [Tarefas_Joao.md](Tarefas_Joao.md) e [Tarefas_Leonardo.md](Tarefas_Leonardo.md).
As decisões de requisitos e o modelo de dados estão em [CityFix_Decisoes.md](CityFix_Decisoes.md).
As regras de negócio, com o estado e o sítio no código, estão em [CityFix_RegrasNegocio.md](CityFix_RegrasNegocio.md).

## 1. Prazos

| Data | Marco |
|---|---|
| 22/11/2026, 23:59 | Entrega no Nónio (cada hora de atraso desconta 5%) |
| 23–27/11/2026 | Defesa funcional, com os dois presentes |

## 2. Princípios

- **Seguir o que foi dado nas aulas.** O molde é o ContactApp e a ficha AMov.P07: MVVM com `ViewModel` e `StateFlow`, ecrãs stateful e stateless, navegação type-safe com rotas `@Serializable`, `UiState` com validações, repositório em `data/` com dados de exemplo.
- **Ecrãs comuns aos três perfis.** O anónimo, o munícipe e o operador usam os mesmos ecrãs sempre que o enunciado o permite. Cada perfil vê apenas o que as regras de acesso deixam.
- **Regras de acesso num só sítio.** Ficam em `utils/AccessRules.kt`, como funções Kotlin puras. O ViewModel aplica-as e entrega ao ecrã apenas valores sim/não; o ecrã só mostra ou esconde.
- **Divisão por ecrã, não por perfil.** Cada ecrã tem um dono, que o faz para os três perfis. Assim os dois não mexem nos mesmos ficheiros.
- **O que ainda não foi dado fica anotado.** As tarefas dessas partes são criadas depois da aula correspondente.

## 3. Ecrãs e o que cada perfil vê

| Ecrã | Anónimo | Munícipe | Operador | Dono |
|---|---|---|---|---|
| Início (dashboard + lista) | 3 números, gráficos da lista pública por estado e categoria, botão «Login» na barra de topo | + botão «Nova ocorrência», filtro «As minhas» para lista e gráficos, perfil e «Terminar sessão» | todas as ocorrências, botões de Categorias, Validação, Perfil e «Terminar sessão» | João |
| Detalhe da ocorrência | sem dados do autor, mapa com zoom afastado | se for o autor: mensagens e reabrir | coordenadas, contactos, histórico, ações de estado | João (ecrã) + Leonardo (`LifecycleActions`) |
| Criar ocorrência | — | conta ativa, ponto obrigatório no mapa | — | João |
| Registo | — | NIF, georreferenciação, deteção de impactos; fica pendente | e-mail da autarquia; fica ativo | Leonardo |
| Perfil | — | altera foto, telefone e deteção de impactos; nome, e-mail, NIF e georreferenciação bloqueados | altera foto e telefone; nome, e-mail e NIF bloqueados | Leonardo |
| Login / MFA / recuperação | — | ✓ | ✓ | Leonardo |
| Validar munícipes | — | — | ✓ | Leonardo |
| Categorias | — | — | ✓ | Leonardo |

## 4. Fase 0 — os dois juntos

Esta fase é o contrato entre os dois. Faz-se antes de cada um seguir com as suas tarefas.

| # | Tarefa | Feito quando |
|---|---|---|
| 0.1 | Completar o modelo de dados e criar `IncidentsList` (como o `ContactsList`) | As classes de [CityFix_Decisoes.md](CityFix_Decisoes.md), secção 4, existem e compilam |
| 0.2 | `IncidentRepository` em `data/` com dados de exemplo, criado na `Application` e passado aos ViewModels por `Factory` (como o `ContactsRepository`) | A lista do início vem do repositório |
| 0.3 | `AccessRules.kt` com as regras do que cada perfil vê e faz | As regras da secção 3 estão escritas como funções |
| 0.4 | Sessão falsa com seletor Anónimo / Munícipe / Operador | O seletor substitui o `Screen.JOAO` / `Screen.LEONARDO` da `MainActivity` |
| 0.5 | `Destinations.kt` + `NavHost` + `Scaffold` com `MainTopAppBar` na `MainActivity` (como no ContactApp) | Navega-se entre o início e um ecrã de teste, com seta de voltar |
| 0.6 | Combinar os parâmetros do `LifecycleActions` e do componente de mensagens | As assinaturas estão escritas neste ficheiro, secção 7 |
| 0.7 | Criar `values-pt/strings.xml` | O texto do título aparece em português num dispositivo em português |

Estado a 11/11/2026: Fase 0 concluída. A 0.6 ficou fechada com a L7 (contratos na secção 7). O detalhe está em [CityFix_Estado.md](CityFix_Estado.md).

A sessão falsa (0.4) e o `NavHost` único (0.5) substituíram o seletor `Screen.JOAO` / `Screen.LEONARDO` e o menu provisório `DevMenuScreen`. A app abre no início e chega-se aos ecrãs do operador e ao registo pelos botões da barra de topo, que dependem do perfil escolhido.

## 5. Divisão

Legenda da coluna Aula: ✅ já dado (inclui a multimédia de fotos, aula #3, e o GPS, os mapas e os layouts adaptativos, aula #4) · 🔒 ainda sem aula (Firebase, WorkManager, sensores, geofence, vídeo)

| # | Tarefa | Dono | Aula |
|---|---|---|---|
| J1 | Início: 3 números do dashboard | João | ✅ |
| J2 | Lista com expansão individual e global, botão de detalhe e filtro por perfil | João | ✅ |
| J3 | Botão "Nova ocorrência" | João | ✅ |
| J4 | Detalhe: estrutura, parte pública, parte do autor, mapa | João | ✅ |
| J5 | Criar ocorrência, com seleção obrigatória do ponto no mapa | João (vídeo: Leonardo, desde 20/11) | ✅ (vídeo feito a 20/11 com a documentação oficial) |
| J6 | Agrupamento a 10 m | João | ✅ |
| J7 | Pedidos de informação e mensagens | João | ✅ |
| J8 | Reabertura | João | ✅ |
| J9 | Restrição dos 10 km para anónimos | João | ✅ |
| J10 | Deteção de impactos | Leonardo (era do João; passou a 20/11) | 🔒 (feita a 20/11 com a documentação oficial) |
| L1 | Login + aviso de conta pendente | Leonardo | ✅ (documentação oficial) |
| L2 | MFA + recuperação de password | Leonardo | ✅ (documentação oficial) |
| L3 | Registo (munícipe e operador), com georreferenciação no mapa e conta no Authentication | Leonardo | ✅ |
| L4 | Perfil | Leonardo | ✅ |
| L5 | Validação de munícipes | Leonardo | ✅ |
| L6 | Categorias | Leonardo | ✅ |
| L7 | `LifecycleActions` | Leonardo | ✅ (vídeo da prova feito a 20/11) |
| L8 | Multimédia: câmara, galeria, Coil (a base foi feita pelo João com a J5; a L3, a L4 e a L7 já a usam) | Leonardo | ✅ (vídeo feito a 20/11) |
| L9 | Compressão, thumbnail, envio com WorkManager | Leonardo | 🔒 (feita a 17/11 com a documentação oficial) |
| L10 | Alerta de proximidade de 20 m (serviço da P10 feito a 16/11; aviso ao autor pelo Firebase) | Leonardo | ✅ serviço; aviso feito com a L11 e testado (17/11) |
| L11 | Notificações, encerramento aos 5 dias, aviso das 24 h | Leonardo | 🔒 (feita e publicada a 17/11 com a documentação oficial; falta testar em dois dispositivos) |
| F1 | Textos EN/PT, landscape e acessibilidade nos seus ecrãs, com a paleta partilhada do CityFix | Cada um | ✅ |
| F2 | Integração, relatório técnico em PDF, ZIP | Os dois | — |

## 6. Ainda por dar nas aulas

Estas partes ficam anotadas. As tarefas são criadas depois da aula. A aula #4 (ficha P10: GPS, permissões, mapas e layouts adaptativos) foi aplicada a 16/11 nas tarefas J4, J5, J6, J9, L3 e F1.

| Tema | Aula | Tarefas afetadas |
|---|---|---|
| Firebase (Authentication e Firestore a 16/11, Storage e Cloud Messaging com Cloud Functions a 17/11, com a documentação oficial) | sem aula; documentação oficial | — |
| WorkManager (usado na L9 a 17/11, com a documentação oficial) | por definir | — |
| Vídeo (gravar, escolher e mostrar; feito a 20/11 com a documentação oficial) | por definir | — |
| Sensores (acelerómetro, usado na J10 a 20/11, com a documentação oficial) | por definir | — |
| Geofencing | por definir | L10 |

## 7. Contratos entre os dois

Os dois contratos estão definidos (tarefa 0.6, concluída a 11/11/2026).

- `LifecycleActions(status: IncidentStatus, closesAt: Long?, canStartWork: Boolean, canResolve: Boolean, onStartWork: () -> Unit, onResolve: (photoPath: String, note: String) -> Unit)`: componente stateless no espaço `operatorActions` do `IncidentDetailScreen`, dentro da secção «Gestão do estado», que só aparece ao operador (`canChangeStatus`).
  - As transições são funções puras em `data/model/IncidentActions.kt` (`workStarted`, `resolved` e `closed`), como a `Incident.reopened()` da J8. Cada uma devolve a ocorrência alterada com o `StatusChange` no histórico.
  - O `IncidentDetailViewModel` (`startWork`, `resolve`) valida com `canStartWork` / `canResolve`, grava e faz `refresh()`, como no `reopen()`. A `MainActivity` atualiza a lista depois de cada mudança.
- `InformationRequests(requests: List<InformationRequestItem>, canRequest: Boolean, onRequest: () -> Unit, onReply: (String) -> Unit)`: componente stateless no detalhe, partilhado pelo autor e pelo operador. «Pedir informação» e «Responder» abrem o diálogo de texto do detalhe. O ViewModel valida as permissões, grava a ocorrência e atualiza o detalhe. A `MainActivity` atualiza a lista após a gravação.

## 8. Calendário

| Quando | O quê |
|---|---|
| 31/10 – 2/11 | Fase 0 |
| 3/11 – 9/11 | Tarefas já dadas (✅) com dados de exemplo |
| Depois de cada aula | Criar e fazer as tarefas anotadas |
| 17/11 – 22/11 | Integração, testes em landscape e PT/EN, relatório, ZIP |

## 9. Forma de trabalhar

- Um ramo por tarefa, com o número da tarefa no nome (por exemplo `j2-lista`).
- Cada pull request é revisto pelo outro antes de entrar no `main`, para os dois conhecerem o código todo na defesa.
- Os pontos "Por confirmar" de [CityFix_Decisoes.md](CityFix_Decisoes.md) são fechados pelo dono da tarefa a que dizem respeito e registados lá.
