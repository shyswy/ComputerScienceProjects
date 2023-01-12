# README

# multi thread & log-in system

## Multi Thread

### design&implement

lwp(light-weight-process)는 서로 독립적으로 실행되고 자원을 공유하지 않으며, 각각 개별적인 주 소공간과 파일스크립터를 가지는 xv6에서의 프로세스들과 다르게 다른 lwp들과 자원, 주소공간 등을 공유하며 유저레벨의 멀티태스킹을 가능하게 해주는 개념입니다. 이러한 개념을 통해 얻는 이점은 여러가지가 있지만 대표적으로 각각의 thread가 자원을 공유함으로써 메모리 공간을 절약 할 수 있다는 점이 있습니다. 또한 각 thread들은 state를 공유하지 않습니다. 예를 들어 하나의 thread가 작업중 io를 wait한다고 같은 프로세스에 속해있는 모든 thread들이 해당 io를 wait하지 않아도 되기에 response역시 좋아지게 됩니다. 이와 같은 특징과 장점을 가지는 lwp를 xv6에서 구현해보도록 하겠습니다.

과제명세의 xv6에서 thread가 프로세스와 비슷하게 취급되어야 한다는 항목을 고려하여, thread의 구조체를 별도로 만들어 구현하는 것이 아닌, 기존의 proc 구조체를 그대로 사용하되, process(master)/thread 를 구분해주는 방식으로 구현하였습니다. process는 속해있는 thread들이 개별적인 process 처럼 동작하는 것이 아닌, 하나의 덩어리처럼 동작할 수 있게 control 해주는 역할을 수행하게 됩니다. 모든 자신에게 속해있는 thread들을 관리하는 process(master)는 여느 프로세스들 처럼 pid를 가지고, 이 pid를 자신에게 속해있는 thread들과 공유하게 됩니다. 이에 따라 ‘같은 프로세스에 속해있는가? ‘ 에 대한 물음은 thread들이 가지고 있는 pid를 통해 확인 할 수 있게됩니다. 또한 process는 기존 parent- child 프로세스 관계에서 parent가 하는 역할중 일부 역시 수행하게 됩니다. parent와 유사하게, process는 thread 들이 종료될때까지 wait하고, thread들 의 뒤처리를 담당하게 됩니다.

또한 xv6에서 메모리 영역에 관한 구현에 어려움이 있어 cscope를 통해 찾던 중, kalloc.c와 같은 가상화된 메모리에 관련된 함수가 존재하는 파일들을 찾아 스레드의 메모리 영역 구현에 이용하 였습니다. 또한 thread들은 기존의 heap과 stack 처럼 상황에 따라 dynamic하게 메모리 영역을 할당 받게 됩니다.

아래 사진은 proc.h입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled.png)

추가된 인스턴스들은 기존 process 구조체에 thread의 특성을 더해주고, 이러한 thread들을 묶어 서 관리하는 process(master)와 구분하여 관리되도록 만들어주었습니다.

tid는 기존 pid와 동일한 역할을 하는 thread id 이고, vir_add는 virtual memory의 주소를 저장하 는 인스턴스입니다.

또한 empty_mem 구조체는 비어있는 memory space를 나타내는 구조체로, 비어있는 메모리 공간 을 표시합니다.                      empty_mem은 비어있는 메모리를 모아둔 자료구조로,

thread를 생성할 때 만약 empty_mem이 비어있다면, 크기를 증가시키고 새로 생성된 top을 thread에게 할당하여 메모리를 virtual하게 할당해줍니다( push).

만약 비어있지않다면,  빈 공간을 할당해줌과 동시에 empty_mem에서 해당 공간을 제거해줍니다 ( pop )

지금부터는 thread가 올바르게 동작하기 위해 구현한, 혹은 구현하라고 명시 되어있는 함수들에 대하여 설명하겠습니다. 우선 proc.c에 구현한 함수들에 대하여 설명하겠습니다.

1) thread_create

아래 사진은 thread_creat함수의 일부분입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%201.png)

이 함수는 기존 xv6의 fork() 함수와 마찬가지로 thread(process)를 생성해주는 함수입니다. 구현은 상당부분을 기존 xv6의 fork함수를 참고하여 구현하였습니다.

우선 thread는 같은 프로세스 내부의 thread간의 자원공유가 가능하여야 합니다.

이에 따라 같은 process 내의 thread들은 pid를 공유하되, 고유한 tid를 가지게 함으로써 같은 process 내부에 위치한 thread들을 표시하면서 각 thread들을 구분 지었습니다.

만약 새로운 thread를 생성할 프로세스에 master process가 존재하지 않는다면, 새롭게 생성되는 thread를 master process로 임명합니다.

thread의 주소를 process(master) 의 메모리에 추가해주게 됩니다. 이전에 설명하였듯이, dynamic하게 해당 주소를 할당한 뒤에, process의 sz를 argument로 넣어줍니다.

2) thread_exit

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%202.png)

해당 함수는 thread의 eixt()을 구현한 함수로, 이 역시 기존 exit() 함수를 참조하여 작성하였

습니다.

저의 구현 방식에선 thread들을 총괄하는 process가 parent의 역할을 대신하기 때문에

기존 exit() 함수가 자신을 생성한 parent를 깨우면 종료되는 것에 반하여, 해당 함수는 thread에서 exit이 발생하면, 속해있는 process(master)를 wake합니다.

3) thread_join

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%203.png)

argument로 지정한 thread가 종료되기를 대기한 뒤, 반환값을 받아옵니다. 또한 스레드가 종료된 뒤, 스레드에 할당된 자원들을 회수하고 정리합니다.

기존의 wait함수와 비슷한 동작을 하기에 wait 함수를 참조하여 구현하였습니다. 우선 join은 모든 thread들을 총괄하는 process만이 요청할 수 있기에 thread에서 요청이 들어 왔다면 바로 return을 하여 예외처리를 해주었습니다. process는 join에서 zombie가 된 process 들을 정리해주고, sleep 하여 모든 하위 thread들의 종료를 대기합니다.

4) thread_wakeup

exec() 함수에 의해 호출되고, pid 와 proc 구조체 하나를 인자로 받습니다.

이때 proc구조체로 주어지는 것은 thread들을 관리하는 process입니다.

해당 함수가 실행된 후에 ptable을 돌며 pid를 통해 해당 process에 있는 thread들을 runnable하게 만들어줍니다.

5) thread_kill

thread의 경우, 만약 1개의 thread가 kill이 된다면, 해당 스레드가 속해있는 process에 속 한 모든 thread들 역시 kill 되어야합니다.

하지만 이는 exit() 함수 내부에 구현하였기에, 해당 함수에선 process(master)가 ptable을 돌며 pid를 통하여 자신에게 속해있는 thread들을 재운뒤 killed flag를 표시해준다.

fork:

fork()함수 또한 몇가지 수정사항이 존재합니다.

thread를 구현함에있어 기존의 proc 구조체를 사용하여 구현하였기에, fork()함수 역시 간단하게 수정해주었습니다. 메모리 영역의 관리를 제외하곤 각각의 thread가 proc구조체를 통하여 개별적 인 process처럼 동작하기에 thread들을 총괄하는 process가 보유한 정보를 가지고 동작하게끔 변 경해주었습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%204.png)

위 사진과 같이 fork함수가 master가 가지고있는 size 정보를 가지고 동작하도록 수정하였습니다.

growproc:

growproc역시 변경점이 존재합니다. 메모리 영역의 관리는 master가 가지고 있는 정보로 동작하 도록 아래 사진과 같이 수정하였습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%205.png)

exit:

exit함수에서의 변경점에 대해 설명하겠습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%206.png)

exit함수에선 이전에 구현하지 않고 넘어갔던 master가 속한 thread들을 모두 제거하는 역할을 추 가적으로 수행합니다.

이 작업은 process(master)가 총괄하여 진행하며, ptable을 돌며 zombie state인 thread 들은 clean 해주고, 그외 thread들은 하나하나 kill 해줍니다.

이후 만약 kill 이 한번도 진행되지 않았다면 break로 곧바로 나가주고, 그 외엔 thread들의 exit 을 기다려야하기에 sleep 하여 대기해줍니다.

### result:

1) thread_test

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%207.png)

- **test1**

create, exit, join 등의 기본적인 기능과 스레 드 간의 메모리 공유를 확인합니다. 스레드 0은 곧바로 종 료되고, 스레드 1은 일정시간동안 wait한 뒤 종료합니다.

- **test2**

thread 내의 fork에 대해 확인합니다. test 결과   자 식 프로세스의 메모리가 부모프로세스에게 아무런 영향을 미치지 않았고, 반대역시 그러하였습니다. 이를 통해 자식- 부모간은 별도로 thread처럼 메모리 공유가 발생하지 않는 다는 것을 확인할 수 있었습니다.

- **test3**

thread가 메모리를 할당받은 뒤 다른 스레드들이 접근하는 것에 문제가 없는지 확인합니다. 각 스레드들이 메모리를 할당받을 때 문제가 발생하지 않음이 확인되었습 니다.

2) thread_execute

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%208.png)

thread에서 exec의 동작을 확인합니다. 생성된 thread가 올바르게 hello_thread 프로그램을 실행한 것을 확인할 수 있습니다.

또한 exec가 실행되는 순간, 다른 스레드들이 모두 종료되어 에러메 세지가 발생하지 않았습니다

3) thread_exit

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%209.png)

thread에서 exit의 동작을 체크하였습니다. thread에서 exit이 호출되면서 해당 thread가 속해있는 프로세스의 다른 thread들 역시 모두 종료됩니다. exiting 출력이후 곧바로 쉘로 빠져나가집니다.

4) thread_kill

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2010.png)

process가 kill 되었을 떄, 그 프로세서 내의 모든 스레드가 종료되는지 확인합니다.

명시된 process 의 thread가 모두 kill되었고, 올바르지 못한 kill이 없어 zombie process 역시 발생 하지 않아 에러메세지가 발생하지 않았습니다.

### trouble shooting

제가 이번 과제를 구현하며 겪었던 가장 큰 문제점은 바로 기존 xv6의 시스템 자체를 건드려야 했다는 부분입니다. 내부적인 구현을 건드릴수록 생각없이 하는 작은 수정이 예기치 못하게 큰 오류를 발생시켜 디버깅에서 어려움이 존재하였습니다. 우선 제가 겪었던 큰 문제들 중하나는 우 선 ‘thread’ 의 구현이었습니다. 첫 시도는 아예 독립적인 thread 구조체를 생성하고, process 내부 에 이 새로운 thread 구조체들을 저장해주는, 제가 배운 직관적인 내용대로의 설계였습니다.

하지만 구현을 하면 할 수 록 기존 xv6가 구현했던 틀을 건드려야했고, 이로 인하여 수많은 에러 들이 발생하였습니다.

xv6 내에서 처음 건드려보는 파트이기도 하였고, 제가 지난과제에서 건드리지 않았던 기본 파일 들( kalloc 등등) 에 접근을 하기도 했으며, proc 구조체가 정확하게 어디서 어떻게 돌아가는지에 대한 지식이 부족하여 골머리를 앓던 중, 과제 명세에서, thread의 특징 몇가지를 제외하면 기존의 process처럼 동작한다는 설명을 보고 난 뒤, 방향을 새로 틀어 proc 구조체에 색을 더함으로써 thread구조를 표현하는 것으로 방향을 틀어 기존 코드의 수정을 최대한 피하며 구현하여 문제를 해결하였습니다.

## Log-in system

### Design& implementation &test

우선 기본적인 login 시스템을 만들기에 앞서 기존의 xv6의 부팅 시스템을 분석하였습니다. Init.c 에서 sh.c 를 실행하여 부팅을 시작하게 되는데, 이 과정에서 sh.c 이전에 login.c 를 먼저 실행하 여 로그인을 한 뒤에, sh.c 를 실행하는 것으로 login에 대하여 구현하였습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2011.png)

위 사진은 init.c의 main문입니다. 위와 같이 sh.c 가 실행되어야할 위치에 login.c 를 exec하 도록 구현하였습니다. 이후에 login.c에서 로그인이 인증되면, 그곳에서 sh.c. 를 exec하게 됩 니다. 또한 추가된 유저들은 userS라는 유저들의 정보를 모아둔 파일에 아이디와 비밀번호가 저장되어, 로그인 할때마다 해당 파일을 참조하여 로그인 정보를 인증 한 뒤, 실행됩니다.

이를 통하여 xv6가 재부팅 되더라도, 새롭게 make가 되지 않는다면 기존에 addUser함수로 추가한 유저정보나, deleteUser을 통해 삭제한 유저정보는 시스템 상에 유지됩니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2012.png)

위 사진은 fs.c 에 선언한 user 구조체입니다. Priv는 유저의 개인정보( id, passward) 를 가져오 는 배열로, 3차원 배열로 선언해주었고 [유저수] [아이디 or 비밀번호] [char 배열] 의 정보를

표현하기 위해 3차원으로 만들어 주었습니다. Using_idx는 ‘현재 사용중인 유저’ 가 priv 배열 상 몇번에 위치해 있는지 알려주는 변수로, get_now() 함수가 이를 사용하여 현재 사용중인 유저 정보를 얻게 됩니다. 이는 나중에 권한비교(현재 사용중인 유저의 권한 check) 에도 사 용됩니다. 그리고 num 변수는 현재 priv내에 몇 명의 유저 정보가 저장 되어있는지 계속해서 갱신하는 변수로써, 유저가 add될 시 +=1, 유저가 delete 될 시에는 -=1 이 되어 유저수에 대한 정보를 유지합니다.   이와 같이 구현한 이유는, pirv는 큐, stack 과 같이 delete시 동적으 로 크기가 조정되지 않기에   배열의 길이 정보가 유저의 수를 알려 줄 수 없고, add, delete역 시 뒤에 쌓거나, 뒤에서 pop을 하는 방식이 아닌, O(n)의 search 통해 빈공간을 찾아 add하거 나 지울 유저를 찾아 delete하는 방식이기 때문에 num 변수를 통하여 따로 개수 정보를 저 장해 나갔습니다.

Myopen, addUser, deleteUser과 같은 함수들은 sys_ 함수에서 일차적인 과정들을 수행 후 fs.c 에 존재하는 함수로 넘어오게 되는데, 이때 userS에서 유저정보를 로드한 뒤, 넘어오게 됩니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2013.png)

위 사진과 같이 sys_ 함수 내에서 give_userS 를 통하여 유저 정보를 로드 시킨 뒤, 함수를 수행함으로써 기존의 유저정보를 통한 작업 수행이 가능해집니다.

### 1-1) User system

최초 시스템을 구축하는 system call 입니다. 만약 이전에 작업하던 정보가 있다면 불러오 고, 아무런 정보가 없다면 root 정보를 생성하는 등 최초의 login system의 전반적인 영 역을 담당합니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2014.png)

위 사진은 sys_open 함수의 일부분입니다. Xv6에 구현되어있는 namei함수를 통하여

userS의 정보를 불러옵니다. 만약 정보가 없다면 (최초 부팅 시가 이 경우에 해당합니다.) create을 통하여 file을 만들고 root 정보를 넣어줍니다. Sys_adduser, sys_deleteuser 함수 역시 위와같이 namei를 통하여 기존에 추가된 정보와 대조한 뒤, 동작하는 방식으로 선처리를 해 준 뒤, 진행하였습니다. 또한 유저는 자신의 디렉토리를 가지기에, sys_addUser에선 유저 생성 시 해당 유저의 디렉토리를 아래와 같이 생성해줍니다.( 해당 디렉토리의 owner는 유저 자신 으로 설정해줍니다). 과제명세에 deleteuser는 별도의 처리를 하지 않는다고 명시되어있기 때 문에 별도로 deleteuser를 통하여 삭제되는 유저의 디렉토리를 제거하거나 하지는 않습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2015.png)

fs.c에선, sys_함수에서 받아온 유저정보들을 토대로 들어온 명령들을 수행하게 됩니다. 아래 사진은 매 부팅시 마다 initialize해주는 myopen 함수입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2016.png)

위에서 설명했듯이, give_userS함수를 통하여 전역으로 선언된 inode 구조체인 userS에

유저정보가 담긴 정보가 들어오게 되고, 만약 유저정보가 비어있다면 root를 가지고 최초정보 를 생성, 비어있지 않다면 기존 유저정보를 load해줍니다. 이후 update함수를 통해 이 변경사 항을 반영해주는데, 이 함수는 뒷부분에서 설명하겠습니다.

아래 사진은 addUser 함수입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2017.png)

Find_empty() 함수는 단순히 priv 배열을 search하여 처음으로 찾아진 빈공간의 index를 리턴 해주는 함수입니다. 만약 빈공간이 없으면 -1을 리턴해주게됩니다. Add를 할 수 없는 예외사 항들을 체크해준 뒤, priv 배열에 추가할 유저 정보들을 strncpy 함수를 통하여 저장합니다.

이후 마찬가지로 update 함수를 통해 수정된 유저정보 사항들을 반영해줍니다. 이후 User의 수를 1 늘려줍니다.

아래 사진은 deleteUser 함수 입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2018.png)

먼저 root를 지우려고 한다면 , return -1 통해 요청을 거부해줍니다.

그리고 cmp 함수를 통하여 내가 지우려는 유저가 존재하는지 find를 해준 뒤, Memset을 통하여 해당 유저의 정보들을 전부 ‘x’ 로 초기화 해줍니다. 그리고 Update 함수를 통하여 정보를 반영한 뒤, user의 수를 1 감소시킵니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2019.png)

위 사진과 같이 기존에 존재하는 유저가 입력으로 들어온 경우 search에 성공하여 delete해 줍니다.

아래 사진은 update 함수입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2020.png)

이 함수는 adduser, deleteuser, myopen 함수의 helper function으로써, 변경된 유저정보를

유저정보를 저장하는 file에 write해줍니다. 이처럼 give_userS함수를 통하여 userS 파일에서 저장 된 유저정보를 load 해오고, fs.c 에서 변경한 유저정보들을 update 함수를 통하여 다시 userS 파일에 write 함으로써, xv6가 재부팅 되더라도 기존의 유저정보들을 유지한채 동작할 수 있게 됩니다.

제가 설명한 것처럼 adduser, deleteuser 를 통하여 변경한 유저정보가 올바르게 상호작용하 는지 확인하기 위하여 최초 로그인시 ‘a’ ‘b’ 유저를 adduser을 통해 생성 한 뒤, deleteuser을 통해 a 유저만 제거한 뒤, 재부팅 뒤에 로그인 시도를 해보았습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2021.png)

위 사진과 같이, 삭제된 a 유저는 그러한 유저가 없다는 문구가 뜨고, 추가된 b유저는 재부팅 되었음에도 올바르게 정보가 유지되어 로그인이 성공한 모습입니다.

지금까지는 유저정보의 수정에 관련된 fs.c 함수들이었고, 이제 이 정보를 토대로 동작하는 login, logout 함수에 대하여 설명하겠습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2022.png)

login에선, login.c로부터 입력된 로그인 정보(유저 정보) 를 받아오고, 해당 유저가 존재하는지

priv 배열을 돌며 serach합니다. 만약 존재한다면, ‘현재 사용중인 유저’ 로 세팅해준뒤, return 0을 통해 login 성공을 알립니다. Logout은 비교적 심플합니다. 현재 사용중인 유저를 0으로 초기화 후, 성공을 알립니다.

아래 사진들은 로그인과 관련된 몇가지 테스트 입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2023.png)

Root의 최초 비밀번호는 0000으로 설정되었기 때문에 올바르지 않은 비밀번호 입력시에 login에 실패합니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2024.png)

이전에 a~I 의 이름을 가진 9명을 유저를 addUser을 통해 추가하여 root포함 10명의 유저가 등록되어있기 때문에 이후의 add는 fail합니다.

### 1-2) File mode, permission

다음 파트는 유저모드와 권한에 관한 구현입니다. 운영체제는 본래 각 파일간의 관계를 모두 파악하고 1대1 대응으로 파일간의 권한에 대해 알아야하지만, 이것은 너무 무겁기에

크게 3가지로 file을 분류합니다. 자신을 생성한 owner와 owner를 제외한 other, 그리고 gruup 이 있지만, 이번 과제에선 owner와 other 2가지 분류만 진행하기에 2개의 케이스에 대한 read, write execute 권한을 각 파일과 디렉토리에 부여합니다.

Root는 최초의 파일 시스템에서 모든 파일 및 디렉토리의 owner이고, 주어진 chmod_test 함수에서 77 과 같이 owner, other 에대한 권한을 변경할 수 도 있습니다.

이러한 별도의 동작이 없다면, open 시스템 콜을 통해 만들어지는

file의 경우 최초에 MODE_RUSR|MODE_WUSR | MODE_ROTH 의 권한을 부여받는데, 이는 owner가 read, write할 수 있으며, other은 read만 가능함을 나타냅니다.

그리고 mkdir로 만들어지는 디렉토리의 경우, 최초에 MODE_RUSR | MODE_WUSR

|MODE_XUSR | MODE_ROTH | MODE_XOTH 의 권한을 가지는데, 이는 owner가 read, write, execute 할 수 있고, other는 read와 execute만 가능하다는 권한을 나타냅니다.

저는 이러한 권한을 체크하기 위해 get_modeBit 함수를 구현하였습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2025.png)

인자로 들어오는 ip는 inode를 통해 받아온 file 정보이고, 뒤에 추가로오는 own_mode, oth_mode 는 각각 ip와 현재 유저의 관계를 분석하여 만약 owner 라면 owner에 대한 권한 bit를, other 이라면 other에 대한 권한 비트를 리턴합니다. 예를 들어 execute를 하기 위한 권한 체크를 하고 싶다면, 인자로 실행할 파일 혹은 디렉토리를 ip 로, MODE_XUSR( owner의 실행)를 own_mode 로, MODE_XOTH(other의 실행) 을 oth_mode 인자로 받고 됩니다.

만약 owner(혹은 root) 라면 ip의 permission을 MODE_XUSR 와 &연산해주어 해당 영역의 비트만 뽑아내는데, 만약 해당 영역의 bit가 0이라면 0, 1이라면 0이 아닌 값이 남게됩니다.

이를 통하여 해당영역의 비트를 찾을 수 있게되고, return해줌으로써 이 함수에서 현재 주어 진 상황에서 적절한 권한 체크를 가능하게 해줍니다.

이 함수를 통해 권한 체크를 한 파트는 과제명세에 나온대로 namex, exec,create, sys_open, sys_chdir, sys_unlink 입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2026.png)

위 사진과 같이, 체크하고 싶은 파트에서 내가 필요한 비트를 extract 해준 뒤, 만약 1이라면 권한이 존재하는 것이므로 그대로 continue하고, 만약 0이라면 종료해줍니다.

아래 사진은 권한을 변경해주는 chmod 함수입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2027.png)

Chmod가 가능한 곳은 root 혹은 mod를 바꿀 대상의 owner만 가능하기에 만약 둘 모두에 해당 되지 않는다면, chmod가 fail됩니다. 그리고 permission을 입력으로 들어온 mode로 변 경을 해줌으로써 대상의 권한을 변경해주게 됩니다.

아래는 mode, 권한에 관련된 몇가지 테스트 케이스 들입니다.

우선 ls 결과창입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2028.png)

유저정보가 저장되어있는 userS는 root를 owner로 가지는 최초의 파일시스템에 의해 생성된 파일로, 최초의 권한은 owner인 root만이 read, write 가능합니다. 이 파일을 가지고 권한 테 스트를 진행해보았습니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2029.png)

위와같이 tst 유저를 추가해준뒤 해당 유저로 로그인 후 userS를 cat하면, 해당 파일을 read( open 에서) 할 수 없다는 문구와 함께 cat에 실패합니다. 이는 최초에 userS의 권한 설 정이 other가 read 할 수 없게 설정되었기 때문입니다.

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2030.png)

하지만 위와 같이 other에도 모든 권한을 허용해주면, userS 파일에게 other로 인식되는 a유 저도 해당 파일을 읽어들일 수 있게 됩니다. userS 의 내용에 대해 간략이 설명하면

User1xpasswardxUser2xpassward 처럼 ‘x’ (디폴트 값)를 통하여 각 유저간의 구분, 그리고 id 와 passward간의 구분을 해주었습니다.

### 1-3) Ls modification

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2031.png)

ls에서   drwxrwx 와 같이 파일인지 directory인지, 그리고 owner과 other에 대한 read write execute 권한을 표시해주도록 ls.c 를 변경해주었습니다. Permission 배열에 기본적으로

drwxrwx의 값을 세팅해주고, 주어진 파일에 따라 값을 변경해줍니다.

만약 디렉토리면 맨 앞의 character을 ‘d’로, file 이면 ‘-‘로 변경해줍니다. 그외의 케이스는 필 요 없지만 이론 수업에서 배운내용대로 남은 케이스는 group을 의미하는 g로 변경해주게 되

었습니다. 그리고 해당 파일의 권한 bit 를 체크하여 만약 해당 권한의 비트가 0이라면( 없다 면) ‘-‘로 해당영역을 변경하여 권한 없을을 표시하였습니다.

Ls가 올바르게 권한을 표시하나 확인을 하기위해, chmod 를 통해 userS의 권한을 변경한 뒤, ls 명령어를 수행해보겠습니다.( 원래의 ls 결과는 앞의 chmod 사진을 참고해주세요.)

![Untitled](READEME%208f9d26ad953a4230abf9a07343679c19/Untitled%2032.png)

53 은    owner의 bit 값이 5 즉 101 을 의미하는 것이고, 이는 read, execute권한이 존재한 다는 것을 의미합니다. 그리고 뒤의 3은 other의 권한이 011, 즉 write, execute권한이 있다는 것을 의미합니다. 또한 userS 는 파일이기에 맨앞에 ‘-‘로 T_file(파일) 임을 명시하기에

Ls 를 통하여 나온 권한 결과 값이 -r-x-wx 가 되고, 이것이 ls 명령어를 통하여 올바르게 출 력됨을 확인 할 수 있습니다.

### Trouble shooting

최초에 가장 어려움을 겪었던 부분은 바로 정보의 동기화입니다. Xv6의 전체적인 코드가 매우 크기에 cscope를 활용하여도 완벽하게 주어진 코드들의 수행순서를 확인 하는 것이 어려웠고, 제가 의도한 타이밍에 정보가 갱신되게 만드는 것에 어려움이 많이 있었습니다. 그 예 중 하 나가 바로 user정보가 load되는 타이밍이었는데요, userS 파일에서 아직 유저정보를 전부 읽 어오지 않았음에도 get_modeBit 함수가 수행되어 올바르지 않은 결과가 수행되었습니다. Printf로 디버깅하는 과정에서 우연히 printf가 들어가면 올바른 정보가 들어가는 것을 보게 되었고, 이를 통해 수행 속도의 차이로 인한 문제일 수 도 있겠다는 생각도 하게 되었습니다.

그래서 제가 생각한 방법은 initial 변수를 통하여 user 정보의 load 가 완료되었는지 check 해주는 것이었습니다. initial 변수는 user정보를 담은 userS가 load되어 동기화되었다면 1로 바뀌게 됩니다. 이를 바꿔주는 시점은 myopen 함수가 끝나는 부분으로, 이를 통하여 load가 완료 되었을 때( 올바른 권한정보가 들어온 시점부터)만 권한 체크를 통한 동작 제한을 수행 하는 것으로 문제를 해결하였습니다. 또한 inode관련 함수들을 사용하는 것에도 어려움이 있

었습니다. 다행이 과제 명세에서 어느정도의 설명은 적혀 있어 어느정도 해결할 수 있었지만, ilock을 걸어놓지 않는 다거나, lock을 걸고 푸는 것을 잊어버리는 등의 실수들은 매우 크게 반영되어 디버깅시에 오류를 찾는것에서 많은 어려움이 있었습니다. 이 경우, cscope를 통해 경로를 타고 가서 ilock에 도달함으로써 제가 실수한 부분들을 찾아낼 수 있었습니다. 또한 기존의 과제들과 달리, 명확한 테스트케이스가 주어지지 않았기에, 어떻게 제가 구현한 코드 가 올바르게 동작할 수 있는지 테스트하는 것 역시 어려움이 있었습니다. 큰 틀에서 결국

권한의 테스트가 가장 어려웠고, 이를 수행하는 것은 결국 read, write, execute 의 수행으로 나눠지기에 어떤 call 들이 read, write execute를 수행하는지 cscope를 통하여 찾고 그 중 제 관점에 직관적인 것들을 통하여 test 함으로써 어느정도 제가 구현한 코드를 체크해볼 수 있

었습니다.
