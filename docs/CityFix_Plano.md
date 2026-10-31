# CityFix — Plano de trabalho

Plano geral do projeto e divisão de tarefas entre João Carvalho e Leonardo Ferreira.
As tarefas detalhadas estão em [Tarefas_Joao.md](Tarefas_Joao.md) e [Tarefas_Leonardo.md](Tarefas_Leonardo.md).
As decisões de requisitos e o modelo de dados estão em [CityFix_Decisoes.md](CityFix_Decisoes.md).

## 1. Prazos

| Data | Marco |
|---|---|
| 22/11/2026, 23:59 | Entrega no Nónio (cada hora de atraso desconta 5%) |
| 23–27/11/2026 | Defesa funcional, com os dois presentes |

## 2. Princípios

- **Seguir o que foi dado nas aulas.** O molde é o ContactApp e a ficha AMov.P07: MVVM com `ViewModel` e `StateFlow`, ecrãs stateful e stateless, navegação type-safe com rotas `@Serializable`, `UiState` com validações, repositório em `data/` com dados de exemplo.
- **Ecrãs comuns aos três perfis.** O anónimo, o munícipe e o operador usam os mesmos ecrãs sempre que o enunciado o permite. Cada perfil vê apenas o que as regras de acesso deixam.
- **Regras de acesso num só sítio.** Ficam em `AccessRules.kt`, como funções Kotlin puras. O ViewModel aplica-as e entrega ao ecrã apenas valores sim/não; o ecrã só mostra ou esconde.
- **Divisão por ecrã, não por perfil.** Cada ecrã tem um dono, que o faz para os três perfis. Assim os dois não mexem nos mesmos ficheiros.
- **O que ainda não foi dado fica anotado.** As tarefas dessas partes são criadas depois da aula correspondente.

## 3. Ecrãs e o que cada perfil vê

| Ecrã | Anónimo | Munícipe | Operador | Dono |
|---|---|---|---|---|
| Início (dashboard + lista) | 3 números, lista pública | + botão "Nova ocorrência", filtro "As minhas" | todas as ocorrências, atalhos para validações e categorias | João |
| Detalhe da ocorrência | sem dados do autor, mapa com zoom afastado | se for o autor: mensagens e reabrir | coordenadas, contactos, histórico, ações de estado | João (ecrã) + Leonardo (`LifecycleActions`) |
| Criar ocorrência | — | ✓ | — | João |
| Registo | — | NIF, georreferenciação, deteção de impactos; fica pendente | e-mail da autarquia; fica ativo | Leonardo |
| Perfil | — | campos bloqueados conforme o enunciado | campos bloqueados conforme o enunciado | Leonardo |
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

## 5. Divisão

Legenda da coluna Aula: ✅ já dado · #3 multimédia · #4 GPS, mapas e layouts adaptativos · 🔒 ainda sem aula (Firebase, WorkManager, sensores, geofence)

| # | Tarefa | Dono | Aula |
|---|---|---|---|
| J1 | Início: 3 números do dashboard | João | ✅ |
| J2 | Lista com cartões expansíveis e filtro por perfil | João | ✅ |
| J3 | Botão "Nova ocorrência" | João | ✅ |
| J4 | Detalhe: estrutura, parte pública, parte do autor | João | ✅ (mapa: #4) |
| J5 | Criar ocorrência | João | ✅ (foto: #3, localização: #4) |
| J6 | Agrupamento a 10 m | João | #4 |
| J7 | Pedidos de informação e mensagens | João | ✅ |
| J8 | Reabertura | João | ✅ |
| J9 | Restrição dos 10 km para anónimos | João | #4 |
| J10 | Deteção de impactos | João | 🔒 |
| L1 | Login + aviso de conta pendente | Leonardo | 🔒 |
| L2 | MFA + recuperação de password | Leonardo | 🔒 |
| L3 | Registo (munícipe e operador) | Leonardo | ✅ (criação da conta: 🔒) |
| L4 | Perfil | Leonardo | ✅ (foto: #3) |
| L5 | Validação de munícipes | Leonardo | ✅ |
| L6 | Categorias | Leonardo | ✅ |
| L7 | `LifecycleActions` | Leonardo | ✅ (prova de resolução: #3) |
| L8 | Multimédia: câmara, galeria, Coil | Leonardo | #3 |
| L9 | Compressão, thumbnail, envio com WorkManager | Leonardo | 🔒 |
| L10 | Geofence de 20 m | Leonardo | 🔒 |
| L11 | Notificações, encerramento aos 5 dias, aviso das 24 h | Leonardo | 🔒 |
| F1 | Textos EN/PT, landscape e acessibilidade nos seus ecrãs | Cada um | ✅ (layouts adaptativos: #4) |
| F2 | Integração, relatório técnico em PDF, ZIP | Os dois | — |

## 6. Ainda por dar nas aulas

Estas partes ficam anotadas. As tarefas são criadas depois da aula.

| Tema | Aula | Tarefas afetadas |
|---|---|---|
| Multimédia: Photo Picker, câmara com `FileProvider`, Coil 3 | #3 | J5, L4, L7, L8 |
| GPS com `FusedLocationProvider`, permissões em runtime, mapas (Maps Compose / OSM), layouts adaptativos | #4 | J4, J5, J6, J9, L3, F1 |
| Firebase: Authentication, Firestore, Storage, Cloud Messaging | por definir | L1, L2, L3, L11 e a troca do repositório |
| WorkManager | por definir | L9, L11 |
| Sensores (acelerómetro) | por definir | J10 |
| Geofencing | por definir | L10 |

## 7. Contratos entre os dois

A preencher na tarefa 0.6.

- `LifecycleActions(...)`:
- Componente de mensagens:

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
