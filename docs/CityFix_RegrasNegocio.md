# CityFix — Regras de negócio

Todas as regras de negócio do CityFix num só sítio: o que a app tem de fazer, onde está no código e de onde vem.
As decisões completas e os pontos por confirmar estão em [CityFix_Decisoes.md](CityFix_Decisoes.md); o enunciado é [AMov.ProjetoKotlin_PT.pdf](AMov.ProjetoKotlin_PT.pdf).

**Legenda do estado:** ✅ implementada · 📝 decidida, por implementar · ❓ depende de um ponto por confirmar

**Como manter este ficheiro:**
- Uma regra nova (do enunciado ou de uma decisão) entra com o próximo número da sua secção.
- Quando é implementada, passa a ✅ e indica-se onde está no código.
- Os números não se reutilizam: uma regra que deixe de valer fica riscada (~~assim~~) com a data e o motivo.

Origem: `I.1.B` e afins são secções do enunciado; `D16` e afins são decisões de [CityFix_Decisoes.md](CityFix_Decisoes.md); `A` a `E` identificam decisões ou pontos por confirmar nesse documento.

## 1. Perfis e acesso

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-01 | O anónimo vê na lista só as ocorrências públicas e não encerradas. | ✅ | `AccessRules.canViewIncident` | I.1.B, D16 |
| RN-02 | O munícipe vê as mesmas que o anónimo e também todas as suas, incluindo privadas e encerradas. | ✅ | `AccessRules.canViewIncident` | I.2.E |
| RN-03 | O operador vê todas as ocorrências. | ✅ | `AccessRules.canViewIncident` | I.3.E |
| RN-04 | No detalhe, quem não é autor nem operador vê categoria, situação, estado, data, descrição, fotos e um mapa com zoom de cidade, sem gestos; não vê o autor nem as coordenadas. | ✅ | `IncidentDetailViewModel.refresh`, `IncidentMap` (`exact = false`) | I.1.C, D17 (16/11) |
| RN-05 | As coordenadas exatas e o histórico de estados aparecem ao operador e ao autor. | ✅ | `AccessRules.canSeeExactLocation`, `canSeeHistory` | D (11/11) |
| RN-06 | Os dados do autor (nome, e-mail, telefone) aparecem só ao operador. | ✅ | `AccessRules.canSeeAuthorData` | I.3.E |
| RN-07 | Uma ocorrência que o perfil não pode ver aparece como "não encontrada". | ✅ | `IncidentDetailViewModel.refresh` | RN-01 a RN-03 |
| RN-08 | Só o munícipe autenticado com conta ativa pode abrir a criação e escolher o ponto da ocorrência para a registar. | ✅ | `AccessRules.canCreateIncident`, `MainScreen`, `ReportIncidentViewModel.saveIncident` | I.2.A, I.2.E |
| RN-09 | Só o operador gere categorias e valida munícipes. | ✅ | `AccessRules.canManageCategories`, `canValidateCitizens` (botões da barra de topo) | I.3.D, I.3.E |
| RN-10 | O botão de registo aparece só a quem não tem sessão. | ✅ | `MainActivity` (`showRegister`) | I.2.A, I.3.A |
| RN-11 | As ações de gestão do ciclo de vida destinam-se ao operador. As mudanças na resposta a um pedido e na reabertura seguem RN-57 e RN-60. | ✅ | `AccessRules.canChangeStatus`, `canStartWork`, `canResolve`; `LifecycleActions` | I.3.E, II.2, II.4 |
| RN-12 | Um anónimo a mais de 10 km da Praça da República, sem permissão de localização ou ainda sem posição não vê o dashboard nem a lista; vê uma mensagem e o botão de login. | ✅ | `AccessRules.anonymousAccess`, `MainScreen` (`AnonymousRestriction`), `CityConstants` | I.1, D3, D5 |
| RN-13 | Um munícipe pendente não inicia sessão e vê um aviso. | ✅ | `LoginViewModel.finishSignIn`, `SessionViewModel` | I.2.A, D6 |
| RN-14 | Só o autor e o operador consultam os pedidos e as respostas no detalhe da ocorrência. | ✅ | `AccessRules.canSeeInformationRequests`, `IncidentDetailScreen` | I.2.E, I.3.E, D (11/11) |
| RN-15 | Só quem tem sessão tem perfil: o botão de conta aparece ao munícipe e ao operador, nunca ao anónimo. | ✅ | `AccessRules.canEditProfile` (botão da barra de topo) | I.2.C, I.3.C |

## 2. Registo, validação e perfil

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-20 | O nome é obrigatório. | ✅ | `Validators.nameError` | I.2.A |
| RN-21 | O e-mail tem de ter formato válido; o do operador tem de terminar em `@aac-coimbra.pt`. | ✅ | `Validators.emailError` | I.3.A, D9 |
| RN-22 | O telefone tem 9 dígitos e começa por 2 ou 9. | ✅ | `Validators.phoneError` | I.2.A |
| RN-23 | O NIF tem 9 dígitos e é único por perfil (o mesmo NIF pode existir num munícipe e num operador). | ✅ | `Validators.nifError`, `UsersList.hasNif` | I.2.A, I.3.A |
| RN-24 | O munícipe registado fica pendente; o operador fica logo ativo. | ✅ | `RegisterViewModel.register` | I.2.A, I.3.A |
| RN-25 | A opção de deteção de impactos só existe para o munícipe. | ✅ | `RegisterScreen`, `ProfileScreen` (`ImpactDetectionSwitch`) | I.2.A |
| RN-26 | O operador ativa um munícipe pendente, vendo o nome, a georreferenciação e o NIF. | ✅ | `CitizenValidationViewModel.activateCitizen` | I.3.D |
| RN-27 | No perfil, o nome, o e-mail e o NIF são de leitura. A georreferenciação do munícipe também é de leitura. Estes valores aparecem em «Dados da conta». | ✅ | `ProfileScreen`, `ProfileViewModel.saveProfile` | I.2.C, I.3.C, D11 (12/11 e 16/11) |
| RN-28 | A foto do registo é opcional; vem da galeria ou da câmara e fica no `picturePath` do utilizador. Se o registo for abandonado, a foto escolhida é apagada. | ✅ | `UserPhotoField` (`UserAvatar`, `PhotoSourceButtons`), `RegisterViewModel.onPictureChanged`, `RegisterViewModel.onCleared` | I.2.A, I.3.A, D (11/11) |
| RN-29 | No perfil altera-se o telefone (validado como no registo), pode trocar-se ou remover-se a foto e, no munícipe, pode mudar-se a deteção de impactos. A foto anterior é apagada ao gravar a alteração. Uma foto nova é apagada se se sair sem gravar. | ✅ | `ProfileViewModel` (`onPhoneChanged`, `onPictureChanged`, `saveProfile`, `onCleared`), `Validators.phoneError` | I.2.C, I.3.C, D11 (12/11) |

## 3. Categorias

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-30 | O nome da categoria é obrigatório. | ✅ | `EditCategoryViewModel.saveCategory` | I.3.E |
| RN-31 | O operador cria, edita, ativa e desativa categorias. | ✅ | `CategoriesViewModel`, `EditCategoryViewModel` | I.3.E |
| RN-32 | Na criação de ocorrências só aparecem as categorias ativas. | ✅ | `CategoriesList.getActiveSorted`, `ReportIncidentViewModel` | I.3.E |
| RN-33 | A ocorrência guarda uma cópia do nome da categoria, para o manter se a categoria for editada. | ✅ | `Incident.categoryName` | D (modelo) |

## 4. Criar ocorrência

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-40 | Categoria, situação, descrição, foto e localização são obrigatórias. | ✅ | `ReportIncidentViewModel.saveIncident` | I.2.E, D12 |
| RN-41 | A visibilidade é pública por omissão e pode ser privada. | ✅ | `ReportIncidentUiState` | I.2.E |
| RN-42 | Uma ocorrência nova começa em Aberto. | ✅ | `Incident.status` (valor por omissão) | II.1 |
| RN-43 | A foto da criação vem da galeria ou da câmara e é guardada com o propósito `REPORT`. | ✅ | `PhotoPicker`, `ReportIncidentViewModel` | I.2.E, D13 |
| ~~RN-44~~ | ~~Provisório: a localização é a georreferenciação do autor ou, se não existir, o centro do município.~~ Substituída pela RN-45 a 16/11. | — | — | D (11/11) |
| RN-45 | O munícipe tem de marcar o local da ocorrência com um toque no mapa. Juntar ou trocar a foto mantém o ponto escolhido. | ✅ | `ReportIncidentScreen`, `MapLocationPicker`, `ReportIncidentViewModel.onLocationSelected` | D12 (revista a 16/11) |
| RN-46 | Um vídeo pode acompanhar ou substituir a foto. | ❓ | J5 | D13, ponto A |
| RN-47 | Uma ocorrência nova a menos de 10 m de uma principal não encerrada da mesma categoria liga-se a ela; a primeira é a principal; as encerradas não contam. O detalhe mostra quantas participações tem o grupo. | ✅ ❓ | `IncidentsList.findGroupPrimary`, `groupSize` | I.2, D18–D22, ponto B (assumido) |

## 5. Ciclo de vida

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-50 | Os estados são Aberto, Pedido de informações, Em resolução, Resolvido e Encerrado. | ✅ | `IncidentStatus` | II |
| RN-51 | Uma ocorrência está ativa em qualquer estado exceto Encerrado. | ✅ | `IncidentsList.getDashboardStats` | D14 |
| RN-52 | Cada mudança de estado fica no histórico, com data, quem a fez e uma nota opcional. | ✅ | `IncidentActions` (`workStarted`, `resolved`, `closed`, `reopened` e pedidos de informação) | I.3.E |
| RN-53 | O início dos trabalhos é a passagem de Aberto a Em resolução, feita pelo operador, e preenche `workStartedAt`. | ✅ | `AccessRules.canStartWork`, `Incident.workStarted` | I.3.E, D27 |
| RN-54 | Resolver só é possível a partir de Em resolução (nunca em Aberto nem em Pedido de informações). Exige foto ou vídeo comprovativo, preenche `resolvedAt` e notifica o autor. | ✅ foto e `resolvedAt`; 📝 notificação; ❓ vídeo | `AccessRules.canResolve`, `ResolveDialog`, `Incident.resolved`; notificação: L11; vídeo: pontos 13 e A | II.4, D (11/11) |
| RN-55 | Cinco dias depois de resolvida, a ocorrência passa a Encerrada e deixa de aceitar alterações. É feito no dispositivo, ao carregar as ocorrências, com `closedAt` = `resolvedAt` + 5 dias. | ✅ | `AccessRules.isClosingDue`, `Incident.closed`, `IncidentRepository.loadIncidents` | II.5, D28 (11/11) |
| RN-56 | O autor é avisado 24 h antes do encerramento. | 📝 | L11 | III |
| RN-57 | O operador pode pedir informação em Aberto ou Em resolução. O pedido guarda o estado atual em `previousStatus` e passa a Pedido de informações. A primeira resposta do autor repõe o estado anterior e limpa `previousStatus`. | ✅ | `AccessRules.canRequestInformation`, `IncidentActions.withInformationRequest`, `IncidentActions.withInformationReply` | II.2, D23, D24, C, D (11/11) |
| RN-58 | O munícipe pode pedir informação numa ocorrência da sua autoria em qualquer estado exceto Encerrado. O pedido mantém o estado. | ✅ | `AccessRules.canRequestInformation`, `IncidentActions.withInformationRequest` | D24, E |
| RN-59 | As mensagens estão associadas a pedidos de informação e as respostas são apenas em texto. A primeira resposta da outra parte marca o pedido como respondido. | ✅ | `AccessRules.canReplyToInformationRequest`, `IncidentDetailViewModel.replyToInformationRequest`, `IncidentActions.withInformationReply` | D25, C, D |

## 6. Reabertura

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-60 | Só o autor reabre, só em Resolvido e só até 5 dias depois de `resolvedAt`; o prazo aparece no detalhe. | ✅ | `AccessRules.canReopen`, `reopenDeadline` | II.4 |
| RN-61 | Uma ocorrência encerrada não se reabre. | ✅ | `AccessRules.canReopen` | II.5 |
| RN-62 | A justificação é obrigatória e fica como nota da mudança de estado. | ✅ | `ReopenDialog`, `Incident.reopened` | II.4 |
| RN-63 | A foto é obrigatória, da galeria ou da câmara, e junta-se às fotos da ocorrência com o propósito `REOPEN`. | ✅ | `ReopenDialog`, `Incident.reopened` | D13 (alterada a 11/11) |
| RN-64 | Ao reabrir, a ocorrência volta a Aberto, o `resolvedAt` passa a `null` e o `reopenCount` aumenta 1. | ✅ | `Incident.reopened` | D26, D (11/11) |
| RN-65 | Não há limite de reaberturas. | ✅ | `Incident.reopened` | D26 |

## 7. Dashboard

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-70 | Os três números são calculados sobre todas as ocorrências, incluindo as privadas, e são iguais para todos os perfis. | ✅ | `IncidentsList.getDashboardStats` | I.1.A, D16 |
| RN-71 | O tempo médio de reparação é a média de `closedAt − createdAt` das ocorrências encerradas; sem nenhuma, mostra "—". | ✅ ❓ | `IncidentsList.getDashboardStats`, `DashboardStatsCard` | D15, conflito 15 |
| RN-72 | O total conta todas as ocorrências. | ✅ | `IncidentsList.getDashboardStats` | I.1.A |
| RN-73 | A percentagem de ativas é ativas / total × 100 (RN-51). | ✅ | `IncidentsList.getDashboardStats` | I.1.A, D14 |
| RN-74 | A secção «Estatísticas» do início mostra juntos os gráficos por estado e categoria e conta as ocorrências que o perfil vê na lista. | ✅ | `IncidentCharts` (`groupingBy`) | D (11/11 e 16/11) |
| RN-75 | O munícipe pode limitar a lista e os gráficos às suas ocorrências ("As minhas"). | ✅ | `IncidentsViewModel.setOnlyMine` | I.2.E |

## 8. Proximidade, sensores e notificações

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-80 | Quando um operador está a menos de 20 m de uma ocorrência ativa que ainda não está Em resolução (Aberto ou Pedido de informações), os autores são avisados. | ✅ deteção e registo; 📝 notificação ao autor (Firebase) | `ProximityService`, `AccessRules.isProximityAlertTarget`, `Incident.withProximityAlert`; ponto 30 | I.3, II.3, D29 (16/11) |
| RN-81 | Há notificações para mudança de estado, mensagens, proximidade e aviso de 24 h. | 📝 ❓ | L11; ponto 32 | III |
| RN-82 | Com a deteção de impactos ativa, uma aceleração vertical brusca sugere abrir uma ocorrência de pavimento. | 📝 ❓ | J10; ponto 31 | I.2.F |

## 9. Localização, mapas e permissões

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-90 | A permissão de localização é pedida em runtime ao abrir a app; sem ela, a nova ocorrência e o acesso anónimo mostram um botão para a pedir. | ✅ | `MainActivity` (`RequestMultiplePermissions`), `ReportIncidentScreen`, `MainScreen` | V |
| RN-91 | A permissão da câmara é pedida antes de abrir a câmara; se for recusada, aparece um aviso. | ✅ | `PhotoSourceButtons` | V |
| RN-92 | O munícipe indica a georreferenciação no registo, no mapa ou com a localização atual; é obrigatória. Guardam-se só as coordenadas. | ✅ | `RegisterScreen` (`MapLocationPicker`), `RegisterViewModel` | I.2.A, D8 (16/11) |
| RN-93 | O autor e o operador veem no detalhe o mapa com o pino exato e as coordenadas. | ✅ | `IncidentMap` (`exact = true`) | I.3.E, D17 (16/11) |
| RN-94 | Todos os perfis com acesso têm um mapa com as ocorrências que veem na lista. Se o perfil não vê a localização exata de todas, o mapa fica com zoom de cidade e sem gestos. | ✅ | `IncidentsMapScreen`, `MainActivity` (`canSeeExactLocation`) | D (16/11) |
| RN-95 | O operador vê no detalhe a distância até à ocorrência e pode abrir o trajeto no Google Maps. | ✅ | `AccessRules.canNavigateToIncident`, `IncidentDirections` | I.3, D (16/11) |
| RN-96 | O serviço de proximidade só corre com a sessão de operador e com permissão de localização. | ✅ | `AccessRules.runsProximityService`, `MainActivity` | I.3, D (16/11) |
| RN-97 | O autor e o operador veem no detalhe quando a equipa camarária esteve a menos de 20 m. | ✅ | `AccessRules.canSeeProximityAlerts`, `IncidentDetailScreen` | I.3, D (16/11) |
| RN-98 | As notificações pedem-se ao arrancar; a localização em 2.º plano pede-se ao operador e ao munícipe com deteção de impactos, depois de uma explicação. | ✅ | `MainActivity`, `BackgroundLocationDialog`, `AccessRules.needsBackgroundLocation` | V |

## 10. Autenticação (Firebase)

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-100 | Inicia-se sessão com e-mail e password e a sessão fica guardada no dispositivo. Pode terminar-se a sessão no topo do início ou no perfil. | ✅ | `AuthRepository.signIn`, `SessionViewModel`, `MainActivity` | I.2.B, I.3.B, IV, D (16/11) |
| RN-101 | O registo pede password (pelo menos 6 caracteres) e confirmação, e o e-mail não pode estar registado. | ✅ | `Validators.passwordError`, `RegisterViewModel` | I.2.A, I.3.A |
| RN-102 | Se o NIF já existir para o perfil, a conta acabada de criar é apagada e o erro aparece no campo. | ✅ | `UserRepository.hasNif`, `RegisterViewModel` | I.2.A, I.3.A |
| RN-103 | A password recupera-se por e-mail a partir do login. | ✅ | `LoginViewModel.sendPasswordReset` | I.2.B, I.3.B |
| RN-104 | A verificação em dois passos (TOTP) é opcional, ativa-se no perfil depois de confirmar o e-mail e passa a ser pedida no login. | ✅ | `ProfileViewModel`, `SecondFactorSection`, `LoginViewModel.confirmSecondFactor` | I.2.B, I.3.B, D10 (16/11) |
| RN-105 | As categorias de exemplo são criadas quando o primeiro operador entra e a coleção está vazia. | ✅ | `CategoryRepository.seedIfEmpty` | D (16/11) |
