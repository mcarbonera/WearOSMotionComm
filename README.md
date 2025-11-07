# Atividade WearOS

Atividade prática para a disciplina de Desenvolvimento de Aplicativos para Objetos Portáveis.

## Descrição

O projeto é semelhante ao código exibido em sala. Utiliza "Data Layer API" para comunicação entre smartphone e smartwatch. 

A diferença está em utilizar o sensor inercial do relógio ao invés de transmitir uma mensagem fixa. Desta forma, a mensagem é a aceleração detectada no relógio.

O relógio também muda a cor de modo proporcional à intensidade de aceleração detectada.

## Testando

Os dois emuladores precisam estar pareados para funcionamento. Essa configuração é feita no "Device Manager" do Android Studio (três pontinhos -> "Pair Wearable").

Para o teste do sensor, abri o "Extended Controls" do emulador do relógio e na seção "Record and Playback" utilizei a configuração "Macro Playback", que simula o relógio se movendo. 

Não consegui testar utilizando o "Device Pose", pois parece haver um bug no emulador, que trava e fecha.