# README

# MLFQ 구현

## Design&Implement

Multilevel queue 와 MlFQ 구현에 앞서, make 를 할 때 두 스케줄러를 구분해주기 위하여 아래와 같이 make file을 수정하였습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled.png)

Make 시 SHCED_POLICY 에 들어가는 값을 통하여 어떤 스케줄러를 사용할지 구현했습니다.

또한 만약 MLFQ 스케줄러를 사용시에는 MLFQ_K의 값을 받아 큐의 개수를 조정하게 만들♘습니 다.

## MULTILEVEL QUEUE SCHDULER

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%201.png)

우선 위와 같이 우선 현재 ptable을 전부 탐색하여 지금 Runnable하고  짝수의 pid를 가지는

process가 들어와있는지 확인합니다. 만약 그렇다면 even_exist 변수에 1을 할당하여 존재한다는 표시 후 다음 코드로 넘어갑니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%202.png)

위 코드는 스케줄러가 cpu에 할당해줄 process를 찾고, cpu에 할당해주는 작업을 수행합니다. 또한 위 코드는 2가지 케이스로 동작하게 됩니다.

- 스케줄러에 짝수 pid를 가지는 runnable한 process 가 존재한다.

이 경우, 앞서 찾은 even_exist 값이 1이므로 해당 else if 문이 실행됩니다. for문은 짝수, 홀수 관계 없이 모든 process들을 탐색하기에, 지금 p에 들어와있는 프로세스의 pid 가 짝수인지 확인 후, 짝수라면 tmp_process에 p를 대입 해준 뒤

Cpu에 바로 할당해주게 됩니다. 이때 할당된 process 는 time interupt를 통하여 이미 들 어온 process가 완료되지 않더라도 주기적으로 cpu에 새로운 프로세스를 할당해주게 됩

니다. 만약 홀수라면, push 통해 제가 만든 queue자료 구조에 중복없이 누적해주고 할당 없이 continue 하게 됩니다.

- 스케줄러에 짝수의 pid를 가지는 runnable한 process가 존재하지 않는다. 이때는 홀수 pid를 가지는 process들의 스케쥴을 관리하게됩니다.

이 경우, even_exist값은 그대로 0이기에, else if 문으로 들어갑니다. Else if 문에서는 현재 p가 홀수의 pid인지 확인하고, 홀수라면 process를 큐에 중복없이

저장하며 for문을 돌게됩니다. 그리고 ptable을 끝까지 돈 뒤 빠져 나오게됩니다. 이때, 큐는 for문을 돌며 process 가 들어온 순서대로 queue에 누적해주게 됩니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%203.png)

이 코드는 제가 추가한 큐 자료구조 입니다.

In_qu는 이미 큐에 중복된 process 가 있는지 pid를 통해 체크 하고, pop함수는 일반적인 큐와 동일하게 돌아가지만, Push 함수는 In_qu함수를 통해 중복된 값이 있다면 push 하지 않아 중복된 프로세스가 큐에 들어가있는 것을 방지합니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%204.png)

위는 queue를 적용하여 스케쥴하는 코드입니다.

큐가 비어있다면 스킵하고, 만약 큐에 process가 있지만 runnable 하지 않을 시, Pop을 해주고 다음으로 넘어갑니다. 그리고 만약 큐의 front에 runnable한 process가 있다면, cpu에 할당해줍니다. Queue의 FIFO 특성을 이용하여 FCFS를 구현하였습니다.

## MLFQ SCHDULER

멀티레벨 피드백 큐 스케줄러는 MLFQ_K로 입력받은 수 만큼의 큐를 가지고 운용되고, 각 큐마다 4*i+2개의 time quantum 을 가집니다. 그리고 이 time quantum을 모두 사용시 상위 레벨큐로

올려주게됩니다. 그리고 만약 최상위큐(K-1)에서 time quantum을 모두 소모한 경우, priority boost 가 일어날 때 까지 더 이상 할당되지 않습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%205.png)

우선 proc.h의 구조체 proc에 몇가지 변수들을 추가하였습니다. 프로세스의

큐 레벨을 알려주는 level, priority, ‘프로세스’ 의 tick을 체크해줄 변수 ticks, 그리고 마지막 큐에서 타임아웃이 일어날

시 그것을 알려주는 last_out 변수를 새롭게 만들었습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%206.png)

위 코드는proc.c에서 구현된 코드입니다. 우선 for문을 돌며 runnable하지 않은 프로세스는 패스 하고, 만약 아직 아무런 프로세스가 할당되어있지 않다면 tmp에 할당하여 initialize 해줍니다.

그리고 제가 proc 구조체에 새롭게 만든 변수 last_out을 확인하게 되는데, 만약 1의 값이 할당되 어있다면 마지막 큐에서 time quantum을 모두 사용한 proces이기에 제외합니다.

그 이후, 만약 탐색중에 더 낮은 레벨의 큐에 할당된 process가 존재한다면 tmp변수에 갱신해주 고, 같은 큐레벨이라면 더 높은 priority를 가지는 process를 tmp에 갱신에주게 됩니다.

for문을 끝까지돌고나면, 할당하지 말아야할 조건들을 만족하고, 가장 낮은 큐에있고 가장 높은

우선순위를 가진 process가 tmp에 들어가있게 되고, 이후 cpu에 해당 process를 할당해주게 됩니 다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%207.png)

위 코드는 trap.c 에 구현된 코드입니다. 이 코드는 큐레벨에 따른 time quantum, 그리고

Priority boost를 구현한 코드입니다. 매 타임 인터럽트가 발생할때마다, 현재 할당되어 있는 process의 ticks이 1씩 증가하게 되어 cpu에 현재 얼마나 있♘는지를 확인할 수 있습니다.

또한 ‘procss’ 의 tick 이 아닌, 전체의 tick을 나타내는 ticks 또한 존재하는데요, 이경우는

Myproc()->ticks 와 같이 proc 구조체에 생성된 것이 아닙니다. 이 전체의 ticks가 100이 될때마다,

모든 큐들을 최하위(L0)큐에 올려주는 작업을 수행하게 되는데요, 이때 만약 마지막 큐에서 timequantum 을 초과하여 last_out의 값이 1이된 process 가 있다면 다시 0으로 초기화하여

Cpu에 할당될 수 있게합니다. 그리고 만약 process가 level에 맞는 time quantum을 전부소모하게 된다면

- 만약 최상위 큐에 위치하지 않는다면, 큐 레벨을 1 증가시킨 뒤 시간을 초기화해줍니 다.
- 만약 마지막 큐에서 time quantum을 전부 쓴 경우 last_out 변수에 1을 할당하여 표시해주어 cpu에 할당되지 않도록 합니다.

## Set priority, get level, yield

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%208.png)

위 코드는 proc.c에 구현되♘습니다. Setpriority는 부모의 priority를 할당해주되, ‘직접’ 만든 자식 프로세스의 priority만 설정할 수 있게 if문을 통하여 구현하였습니다. Getlev 또한 현재 프로세스 의 level을 리턴하도록 구현하였습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%209.png)

위 코드는 syscall.c 에서 구현하였습니다. Yield와 sleep 시스템콜을 사용한 경우, 프로세스가 작업 을 마친 것으로 간주해야되기 때문에, 해당 시스템 콜이 호출될경우 프로세스의 레벨과 tick을 초 기화합니다.

## Test&Result

ml_test 1

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2010.png)

짝수 프로세스가 먼저 스케줄되고, round robin 방식으로 계속해서 프로세스가 바뀌며 돌아갑니 다

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2011.png)

홀수 pid를 가지는 프로세스의 경우, 마지막에 할당되고, 프로세스가 끝날때까 지 cpu를 차지하는 FCFS 방식으로 스케줄러가 동작합니다.

ml_test2

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2012.png)

짝수인 프로세스들은 yield될때마다 번갈아 실행되어 유사한 시간에 끝 나고, 홀수인 프로세스들은 yield되어도 다시 가장 낮은 pid의 프로세스 가 할당됩니다.

ml_test 3

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2013.png)

sleep 상태가되면 프로세스가 할당될 수 없기에 다음 조건에 맞는 올바른 프로세스가 할당되는 것을 확인 할 수 있습니다.

**MLFQ_test 1,2**

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2014.png)

큐의 레벨이 올라감에 더 높은 time quantum을 가지고, 그 결 과 더 오랜시간 동안 머무는 것을 확인 할 수 있습니다. 또한 모든 프로세스들이 큐에 머무는 시간이 비슷한 것 또한 확인 할 수 있습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2015.png)

Pid가 더 높은 프로세스가 먼저 나오지만, 결국 전체적인 시간사용량은 유사하기에 비슷한 시간에 종료됩니다.

**MLFQ_test3**       

   

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2016.png)

                               

**MLFQ_test4**      

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2017.png)

Sleep, yield 시스템콜 호출시 프로세스의 시간과 레벨이 초기화 되기에 계속해서 최하위큐에 머무는 것을 확인 할 수 있습니다.

**MLFQ_test 5**

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2018.png)

각 프로세스가 자신이 있고 싶어하는 큐에 편향되게 위치한 것을 확인 할 수 있습니다

**MLFQ_test 6**

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2019.png)

함수가 에러없이 작동함을 확인할 수 있습니다.

## Trouble Shooting

우선 makefile에서 많은 시행착오가 있♘습니다. 과제에 명시된대로 make 시에 값을 받아서 코드 에 적용시켜야 했는데, 이를 어떻게 구현할지에 고민을 하기위해 makefile을 수없이 수정하였는데, makefile에선 사소한 변화도 크게 작용되어 많은 디버깅이 필요하였습니다.

또한 pid가 1,2 인 프로세스들에 대한 예외처리에서도 많은 문제가 발생하였습니다.

Pid가 1인 프로세스와 2인프로세스는 각각 init, shell 프로세스로, 두 프로세스 모두 필수적이기에 스케줄러에 들어가 다른 프로세스들과 동일한 취급을 받으며 스케줄링될시 에러가 발생하게 되어, if문을 통하여 예외처리를 해주♘습니다. MULTILEVEL QUEUE 에서 FCFS 스케쥴시에 문제가 발생 하였습니다. 

처음엔 단순히 먼저 들어온 process의 아이디는 뒤에 들어온 프로세스보다 낮을 것 이라는 이론을 통해 pid가 낮은 것을 우선적으로 할당하는 방식을 사용하였지만, 만약 pid= 1,3,5 인 프로세스가 sleep한뒤, 5,3,1 순으로 깨어났을 때, 5,1,3 순으로 깨어나는 등, pid 기반의 방식에 는 한계를 발견하여 이를 queue 자료구조를 사용하여 먼저 들어온 process를 우선적으로 할당하 도록 하여 문제를 해결하였습니다.

또한 MLFQ을 구현할 때에, 처음에는 마지막 큐에서 time quantum을 모두 소모한 프로세스를 SLEEP 상태로 만들어주고, priority boost에서 wake 시켜주는 방식을 통하여 구현하려고 하였습니다.

하지만  yield 를 통해 cpu를 넘겨줄 때에 해당 함수에서 process 의 상태를 RUNNABLE하게 만드는 것을 확인하고, 프로세스 구조체인 proc에 마지막 큐에서 time quantum을 모두 소모한 프로세스를 따 로 표시하여 예외처리를 하는 방식으로 해결하였습니다. 

또한 ‘tick’ 에 대한 개념의 혼동이 제가 과제를 구현할 때 가장 큰 문제가 되었는데, Process 가 cpu를 사용한 시간의 개념으로 작용하는 tick과, 전체 시간의 흐름을 나타내는 tick의 개념을 계속해서 혼동하여 구현시 많은 에러가 발생 하였습니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2020.png)

이 코드는 제가 추가한 큐 자료구조 입니다.

In_qu는 이미 큐에 중복된 process 가 있는지 pid를 통해 체크 하고, pop함수는 일반적인 큐와 동일하게 돌아가지만,

Push 함수는 In_qu함수를 통해 중복된 값이 있다면 push 하지 않아 중복된 프로세스가 큐에 들어가있는 것을 방지합니다.

아래는 queue를 적용하여 스케쥴하는 코드입니다.

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2021.png)

![Untitled](README%20cdd6b75f55974020bcf0eb4eefd6ab14/Untitled%2022.png)