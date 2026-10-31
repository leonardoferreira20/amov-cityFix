# CityFix — Trabalho prático de Arquiteturas Móveis (ISEC 2026/27)

Instruções para qualquer assistente de IA que trabalhe neste projeto (Claude, ChatGPT/Codex ou outro).

## Contexto
- Aplicação Android em Kotlin para registo e acompanhamento de ocorrências urbanas num município.
- Trabalho de grupo (2 alunos: João Carvalho e Leonardo Ferreira), com Git.
- Entrega até às 23:59 de 22.11.2026; cada hora de atraso desconta 5%.
- Defesa funcional obrigatória e defesa oral individual: cada decisão de código tem de ser explicável pelos dois alunos.

## Forma de trabalhar
- **Os alunos escrevem o código.** Não alterar ficheiros de código por iniciativa própria. Mostrar o código no chat, explicar as decisões e rever depois de o aluno o escrever.
- Só editar código quando o aluno pedir explicitamente que seja o assistente a fazê-lo, e só alterações simples. Pedidos como "mostra", "como fica" ou "liga X a Y" são pedidos para mostrar código, não para editar.
- Os documentos em `docs/` podem ser atualizados quando o aluno o pedir.
- Propostas pequenas e incrementais. Responder ao que foi pedido, sem antecipar camadas que ainda não são precisas.
- Seguir os padrões dados nas aulas, descritos em `docs/Padroes_Aulas.md`. Se uma solução precisar de algo que não foi dado, avisar antes de o propor.
- Fazer commit ou push só quando o aluno pedir.

## Fontes de verdade
- `docs/AMov.ProjetoKotlin_PT.pdf`: enunciado do professor. Os requisitos vêm daqui.
- `docs/CityFix_Decisoes.md`: decisões já tomadas, conflitos com o enunciado, pontos por confirmar e modelo de dados.
- `docs/CityFix_Plano.md`: plano de trabalho, ecrãs, divisão entre os dois e calendário.
- `docs/Tarefas_Joao.md` e `docs/Tarefas_Leonardo.md`: tarefas de cada aluno.
- `docs/Padroes_Aulas.md` e `docs/AMov.P07.pdf`: padrões e código dados nas aulas.
- Não inventar requisitos. Se algo estiver na secção "Por confirmar" ou não estiver no enunciado nem nas decisões, perguntar antes de implementar.
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
