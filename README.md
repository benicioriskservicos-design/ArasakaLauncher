# ARASAKA LAUNCHER

Launcher Android nativo inspirado na estética corporativa cyberpunk. Este projeto é independente e não contém arquivos oficiais, marcas gráficas ou recursos extraídos de Cyberpunk 2077.

## Recursos da versão inicial
- Pode ser escolhido como aplicativo de tela inicial padrão (HOME).
- Tela inicial temática em preto/vermelho.
- Gaveta com aplicativos instalados, ícones reais e busca.
- Atalhos para abrir aplicativos.
- Personalização de cor de destaque e fundo.
- Painel com informações do dispositivo.
- Funciona localmente; não exige conta ou conexão com a internet.

## Como gerar o APK sem Android Studio
1. Crie uma conta gratuita no GitHub, se ainda não tiver.
2. Crie um repositório novo chamado `ArasakaLauncher`.
3. Extraia este ZIP e envie **todo o conteúdo** para a raiz do repositório.
4. Abra a aba **Actions** no GitHub. O workflow `Build Android APK` será executado automaticamente.
5. Quando terminar com status verde, abra a execução e baixe o artefato **ArasakaLauncher-debug-apk**.
6. Extraia o arquivo baixado. O APK estará em `app-debug.apk`.
7. Envie o APK para o celular e instale. O Android pode pedir autorização para instalar apps dessa fonte.
8. Pressione o botão Home e escolha **ARASAKA LAUNCHER**. Se aparecer a opção, selecione **Sempre**.

## Observações
- Este ZIP contém o projeto-fonte e a automação de compilação, não um APK já compilado.
- A versão inicial usa recursos nativos do Android e não precisa de internet durante o uso.
- O launcher personaliza a tela inicial, gaveta e atalhos. Telas de Configurações, notificações e bloqueio continuam sob controle do Android.
- O Android pode limitar a visibilidade de alguns aplicativos ou perfis de trabalho.
- A versão debug é para testes. Para distribuição ampla, será necessário configurar assinatura própria (release).

## Compatibilidade
- Android 8.0 (API 26) ou superior.
- Compile SDK 35.
