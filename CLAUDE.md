# CityFix — Trabalho prático de Arquiteturas Móveis (ISEC 2026/27)

## Contexto
- Aplicação Android em Kotlin para registo e acompanhamento de ocorrências urbanas num município.
- Trabalho de grupo (2 alunos), com Git.
- Entrega até às 23:59 de 22.11.2026; cada hora de atraso desconta 5%.
- Defesa funcional obrigatória e defesa oral individual: cada decisão de código tem de ser explicável pelos dois alunos.

## Fontes de verdade
- `docs/enunciado.pdf`: enunciado do professor. Os requisitos vêm daqui.
- `docs/CityFix_Decisoes.md`: decisões já tomadas, conflitos com o enunciado, pontos por confirmar e modelo de dados.
- Não inventar requisitos. Se algo estiver na secção "Por confirmar" ou não estiver em nenhum dos dois ficheiros, perguntar antes de implementar.
- Quando uma decisão nova for tomada, atualizar `docs/CityFix_Decisoes.md`.

## Comunicação
- Responder em português de Portugal, sem vocabulário do português do Brasil.
- Direto, sem enchimento.

## Tecnologias exigidas pelo enunciado
- Kotlin e Jetpack Compose.
- Arquitetura MVVM, com separação entre composables stateless e stateful.
- Firebase: Authentication (e-mail/password, MFA, recuperação por e-mail), Cloud Firestore com cache offline, Storage, Cloud Messaging.
- WorkManager para o envio de fotos e vídeos, condicionado à existência de rede.
- Imagens comprimidas e com thumbnail antes do envio.
- Permissões pedidas e validadas em tempo de execução: localização em primeiro e segundo plano, câmara, notificações.
- Inglês por omissão e uma segunda língua (português) segundo o dispositivo.
- Funcionamento em portrait e landscape.
- Mecanismos de acessibilidade.

## Regras de código
- Código limpo e minimalista; sem comentários que repetem o código.
- Nomes de classes, campos e funções em inglês.
- Textos visíveis em `strings.xml`, nunca fixos no código.
- ViewModels sem referências a Activity, View ou Context.
- Não acrescentar bibliotecas externas sem perguntar.
- Ao propor código, explicar as decisões, porque os dois alunos têm de as justificar na defesa.

## Entrega
- ZIP único no Nónio, com o nome `AMOV2627.<nr_aluno1>.<nr_aluno2>`.
- Remover do ZIP `.gradle`, `build`, `app/build` e `local.properties` (penalização até 5%).
- Incluir o relatório técnico em PDF.
