# README

이번 프로젝트에서는 On-disk B+ 트리의 구조를 분석하고 B+트리가 가지는 

Modification Overhead를 개선하였습니다.

# MileStone 1

우선 기존 On-disk B+에 대한 구현과 분석입니다.

## Introduction

B+ 트리는 B트리와 다르게 key 값에 value 값이 붙어 record형식을 취하고, leaf 노드에 실질적인 record들이 들어있고, 모든 record값들이 포함됩니다. 그리고 그 상위의 internal 노드들에는 record가 들어가있는 leaf로 가는 위치를 보여주는 key 값들이 들어있게 됩니다.

각 key들은 하나의 기준이 되어 해당 key의 왼쪽에는 해당 값보다 작은 값들을, 오른쪽에는 해당 값보다 큰 값들이 존재합니다.

이러한 형식으로 인해 leaf node 에선 leftmost record의 key 값이 부모 key값과 동일합니다. 아래는 선언된 구조체들입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled.png)

설명에 앞서 모든 디테일한 설명들은 각 코드 주석에 적어 놓았기에 참고해주시면 됩니다. Record type은 bpt.h에 typedef로 정의되어 있습니다. Key값과 Value 값을 보유 중입니다.

각 페이지는 해당 노드가 leaf 인지 확인하는 boalean 타입과, 보유한 (key, 오른쪽 자식)을

각각 Key, offset으로 가지는 I_R이라는 구조체를 저장하는 배열을 보유하는데, 이때 leftmost key의 Left에 위치한 offset 정보가 존재하지 않기에 next_offset에 따로 저장해줍니다.

## Find

- Db_find

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%201.png)

해당 key가 존재하는지 찾고 value를 리턴 합니다. 올바르게 찾지못하면 NULL을 리턴합니다.

- Find leaf

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%202.png)

파라미터로 들어온 key가 위치할 적절한 leaf 노드(page)의 offset을 찾아서 리턴 합니다.

## Insert

- Db_insert

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%203.png)

insert 명령의 기본입니다. Key를 넣을 위치를 찾은 뒤, leaf노드에 key를 추가해줍니다.

만약 아직 트리에 아무것도 들어가 있지 않다면, 새로운 root를 생성한 뒤 key, value를 넣고 리턴 하고, 이미 tree에 존재하는 값을 insert하면 안되기에 미리 db_find로 해당 key가 존재하는지 체 크해줍니다. 만약 없다면, find_leaf 함수를 통해 입력 값이 위치할 leaf 노드를 찾습니다.

이 외의 경우엔 2가지 케이스가 존재합니다.

key가 추가된 노드에서 최대 개수(M) 보다 작아 split이 불필요 하다면 insert_into_leaf를 통해 실 제 insert를 진행합니다.

만약 최대개수 초과할 시 분할(split)이 발생합니다. 이때는 insert_into_leaf_as 함수로 타고 들어가 게 됩니다.

- Insert_into_leaf

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%204.png)

Split이 필요 없이 단순 insert하는 메소드입니다.

값이 위치할 적절한 leaf node를 파라미터로 받았고, 이 leaf 안에서 해당 record가 위치할 위치를 찾은 뒤, insert를 진행합니다. 이후 write을 통해 변경사항을 write 해준 뒤 leaf를 리턴하며 종료 됩니다.

- Insert_into_leaf_as

Split 이 발생하는 insert에 대한 메소드 입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%205.png)

왼쪽 노드, 중앙 키 값, 오른쪽 노드로 나눈 뒤 중앙 키 값을 부모로 올린 뒤 해당 키의 왼쪽, 오 른쪽에 각각 연결해줍니다. 이때 부모key에 대응하는 값이 leaf에서 사라지게 되므로 모든 leaf를 뒤져보면 모든 record (key,value)를 얻을 수 있어야 한다는 조건이 깨지게 됩니다.

따라서 split된 inorder successor(오른쪽 자식의 leftmost)에 중앙값을 넣습니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%206.png)

Leaf에 대한 작업을 수행하고, split되어 빠진 중앙값을 부모 노드에 넣는 작업을 수행합니다.

- Insert into parent

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%207.png)

여기서부터 재귀적인 특성이 보이기 시작합니다. 부모에게 키를 추가했는데, 그 부모가 또다시 사 이즈 초과로 split되어 부모에 key를 추가하고, 또 추가를 반복하며 root를 타고 갈때까지 재귀적 인 반복이 될 수 있는 코드입니다. 부모에게 중앙값 key를 insert 해주는데, leaf에 추가할 때와 마 찬가지로 split 되거나, 되지 않거나 2가지 케이스로 나누어 insert합니다. 모든 부모 노드들은 leaf 가아닌 internal 노드이기에 insert_into_internal로 split이 발생하지 않고 internal 노드에 넣는 메 소드를 구현하고, insert_into_internal_as로 split 이 발생하며 internal 노드에 insert 하는 케이스를 구현합니다.

- insert_into_internal

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%208.png)

부모 노드에 단순 insert를 수행해주면 됩니다.

- insert_into_internal_as

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%209.png)

부모에 중앙값을 insert함으로 또다시 초과가 발생하여 split하는 case를 다룹니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2010.png)

재귀적으로 다시 parent에 key를 넣는 과정을 요청합니다. 이는 부모에게 중앙값을 insert해도 split이 일어나지 않을 때까지 반복되며 상위로 이동할 것입니다.

## Delete

우선 삭제하기 전, 삭제할 키가 없거나, 해당 트리 루트가 비어 있는 경우를 예외처리해 준 뒤, Delete_entry에서 본격적인 삭제를 처리합니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2011.png)

- Remove_entry_from_page

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2012.png)

해당하는 key를 단순히 삭제합니다. 모든 delete는 단순히 key를 지운 뒤, b+ 트리의 조건이 위배 되었다면 tree modification을 수행하는 것으로 진행됩니다.

- Delete_entry

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2013.png)

우선 앞서 설명한 remove_ entry_from_page 함수를 통해 key 가 위치한 leaf 가서 단순히 해당 key를 제거합니다.

Key를 제거했는데 key최소 개수(m/2 이상) 만족하고, non-leaf(internal) 에 해당 key가 없다면 (leaf의 leafmost에 위치한 key 가 아니다) 단순 삭제가 가능합니다.

Adjust root는 root에서 구조가 변경할 시 root 구조를 조정하는 메소드입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2014.png)

Key 제거했는데 최소개수(m/2 이상) 만족하지 않을 시에는 두가지 케이스로 나뉩니다.

2-1)     이웃 node( 왼쪽 노드)의 key 개수 +삭제된 노드 key 개수가 최대조건(m 이하) 만족시 이웃 트리와 key가 삭제된 트리 merge(coalse) 수행

2-2)     위조건을 만족하지 않는다면 tree redistribution을 수행합니다. 이는 neighbor 에 게서

가장 인접한 key를 빌려오는 과정입니다.

- Merge

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2015.png)

neighbor 노드(왼쪽 노드)에 key가 삭제된 노드를 merge합니다.

단 여기서 주의할 사항이 leftmost의 경우 오른쪽이 neighbor이 되게 예외처리가 되어있기에 이 를 고려하여 구현되었습니다. 이후 부모에서 병합되어 사라진 노드(오른쪽 노드) 를 가르쳤던 key

제거합니다.

케이스는 두가지입니다.

3-1) internal 노드에서 merge 발생. 이때는 neighbor 노드에 부모키를 삽입 하고, 그 뒤에 쭉 key 가 삭제되어 병합될 노드들의 값을 넣어 병합합니다.

3-2) leaf 노드에서 merge 발생. 이때는 병합될 노드( 오른쪽 child) 의 leftmost에 부모키에 해당 하는 값이 존재하기에 부모키를 사이에 끼워줄 필요없이 neighbor노드에 key가 지워진 노드를 결 합해줍니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2016.png)

두개의 노드가 merge되면, 두개 노드의 부모는 1개의 자식을 잃었이기에, 부모에서 사라진 노드의 key를 제거하는 과정을 delete_entry를 재귀적으로 호출하여 수행합니다.

- tree redistribution

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2017.png)

merge를 수행하면 최대 key 개수를 넘어버릴 경우, 형제키를 빌려옵니다. 두가지 케이스로 나뉩니다.

4-1) Internal node에서 발생: 부모key 값을 넘겨주고, 이웃 노드의 인접키를 부모키로 올린다. Leftmost의 경우를 제외하면 부모key 값을 오른쪽 자식의 leftmost에 넘겨주고, 이웃 노드 의 rightmost를 부모키로 올린다.

4-2) Leaf node에서 발생: 이웃에서 가장 인접한 키를 가져와서 넣고, 해당 key를 부모 key에도 넣는다. Leftmost의 경우를 제외하고 말하면 부모키의 key값이 오른쪽 자식의 leftmost에 존재. (leaf의 leftmost는 부모key값) 이웃의 rightmost를 부모키, 맨 앞에 넣어주면 끝. 부 모키에 넣는 건 key만, 맨 앞에 넣는 건 record 값.

단 leftmost의 경우는 neighbor이 오른쪽으로 예외처리 되있습니다. 따라서 좌우를 뒤집어서 그대 로 적용하면 됩니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2018.png)

모든 작업이 완료되면, write하여 변경사항을 저장 후, 리턴 합니다.

## Development

- **Delay merge**

키를 삭제한 뒤에 merge를 최대한 지연시켜 실질적인 merge 작업 수행횟 수를 줄이는 방식입니다. key 가 delete 가 일어났을 때, merge가 필요하더라도 logical하 게 했다고 가정한 뒤, 실질적인 merge는 가능한 미룹니다. search key 등 tree의 값을 가 져와야 하는 경우가 발생할 때까지 실제 delete에 의한 merge를 수행하지 않고, 그 사이 에 key가 지워진 노드가 merge 하지 않아도 될 정도로 insert 되면 해당 트리는 실질적 인 merge를 수행하지 않고 모자란 키를 새롭게 insert받은 키로 채우면 됩니다.하지만 만 약 그전에 search 등 실질적인 트리 접근 요청되어지면, 그 때 미뤄둔 실제 merge를 수 행하여 올바른 값을 받아올 수 있게 합니다. 이를 통하여 실질적인 tree modification 수 행횟수가 줄어들고, 이는 disk I/O 횟수 감소와 직결되어 b+ tree의 overhead를 상당 부분 감소시킬 수 있습니다.

- **cache 메모리**

어떠한 key의 위치에 대한 cache 메모리를 생성하는 것입니다. disk I/O가 발생하면 cache 메모리에 disk에서 가져온 key와 그 위치를 기록하고, 만약 특정 key를 search 하거나 제거할 때, 해당 key의 위치를 cache메모리에서 가져옵니다. 만약 key가 삭제, 변경될 경우, cache 메모리에 변경사항을 반영하고, 만약 cache메모리가 가득 차 공 간을 마련해야하는 경우, LRU를 통하여 가장 재사용 가능성이 낮을 가능성이 높은 정보를 cache 메모리에서 내보냅니다. 이때, 쫓겨난 데이터는 disk에 I/O 되어야 하며 이때 모든 변경사항들을 반영해야 합니다. 이를 통해 실제 disk I/O 횟수를 감소시킬 수 있고, 짧은 시간 내에 한번 find를 수행하여 cache에 위치 정보가 들어있는 key의 경우, find함수를 통해 찾는 과정 없이 바로 위치를 받아올 수 있어 overhead가 감소합니다.

# MileStone 2

Milestone 2 에서는 기존의 B+ 트리 코드에서 Tree Modification 이 발생할 때마다 생기는 Disk I/O 로 인한 overhead들을 개선시키는 방법에 대하여 설명하겠습니다.

## Introduction

기존 B+ 트리는 사전에 정의한 Internal 과 leaf의 최소 key 개수, 최대 key 개수를 기준으로 이를 어긋나는 input 이 들어오는 순간 즉각적으로 split, Merge, redistribution 등의 tree modification 연산이 발생하게 되고, 이는 tree 구조를 변경하므로 disk에 변경된 tree 정보를 기입하거나 받아오며 Disk I/O를 발 생시킵니다. Disk에 필요한 data를 write 하거나, 필요한 data를 read하는 것은 disk의 head를 필 요한 위치까지 옮기는 physical 적인 작업이 필요하고, 이는 in-memory 상황에서의 I/O에 비해 막대한 시간이 소요됩니다. 이러한 Disk I/O를 절감시키기 위한 가장 확실하고 직관적인 방식은 Cache memory 등 in-memory를 활용하여 다시 쓰일 가능성이 높은 data들을 LRU와 같은 방식으 로 선별한 뒤, 불필요한 disk I/O를 줄이는 방법 등이 존재하지만, 이번 Milestone 에서는 이러한 방식을 배제하고 순수하게 B+ 트리 자체의 알고리즘에 대한 개선 사항에 대해서만 작성하겠습니 다.

## Design & Implementation

### Design

저는 B+ 트리의 tree Modification에서 발생하는 disk I/O를 줄이는 방식을 크게 2가지로 분류하였 습니다.

- 1번의 Tree Modification에 발생하는 disk I/O 횟수를 줄이기.
- Tree Modification 횟수를 최소화하기

먼저 1번 방식, 즉 1번의 Tree Modification에 발생하는 disk I/O 횟수를 줄이기 방식의 경우, 앞서 설명한 Cache memory등을 활용하는 방식이 있으나, 이는 tree 자체에 대한 improvement와는 결 이 다르다고 생각되어 배제했습니다.

따라서 제가 선택한 것은 2번 방식, 즉 문제가 되는 tree Modification 자체의 횟수를 최소화하는 것입니다. 제가 처음 생각한 것은 internal, leaf 노드의 최소 key 개수를 감소시키는 것입니다.

Tree modification 중 key delete 시 발생하는 redistribution과 Merge의 경우, 최소 key 개수 이하 로 내려가게 만드는 delete 연산시에 수행되며, 많은 disk I/O 가 발생합니다.

따라서 최소 key 개수를 감소시킨다면, delete 연산 수행 시 tree modification을 발생시키는 조건 이 완화되어 disk I/O가 감소하게 될 것입니다.

하지만 이 방식의 경우, tree balancing이 자주 발생하지 않게 되고, 결국 tree 높이의 증가로 이어 질 수 있는 tradeoff가 생길 수 있습니다.

따라서 제가 실제로 적용한 방식은 logical delete를 사용하여 Merge 연산을 가능한 delay 시키는 것입니다. 우선 제가 logical delete를 사용할 영역은 오로지 Leaf Node(page)로 국한됩니다.

그 이유는 어차피 모든 실제 insert, delete는 leaf에서 시작해서 그 부모 노드를 타고 올라가며 적 용되기에, 애초에 leaf 부터 logical하게만 지워주고 physical하게 지우지 않는다면, 상위의 internal 노드 안에 존재하는 key들 역시 지워지지 않기 때문입니다.

자세한 설명은 제가 구현한 코드와 함께 이야기해보겠습니다.

### Implementations

우선 기존의 record 구조체에 변경사항이 있습니다. 변경사항은 아래와 같습니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2019.png)

기존의 record에 logical_del이라는 short 타입의 변수가 추가되었는데, 이는 해당 record가 logical 하게는 지워진 상태라는 것을 의미합니다.

아래는 on-disk B+ 트리 코드 중 실제로 leaf에서 해당 key를 가진 record를 지우는 remove_entry_from_page() 함수와 제가 추가한 사항입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2020.png)

만약 delete 연산을 수행하는 중 해당 delete로 인하여 최소 key 개수 이하의 key를 가지게 되어 tree modification이 필요하다면 해당 key를 보유한 leaf 노드에서 해당 key를 logical 하게만 delete 하고 실제 delete는 수행하지 않습니다.

logical delete의 방식은 record 구조체에 key 와 value 외 logical delete 라는 0 or 1을 가지는 short type을 하나 선언하여 해당 변수가 1이라면 아직 해당 key가 physical 하게 지워지진 않아 여전히 record에 남아있지만 logical 하게 지워진 채 남아 있다는 뜻입니다.

따라서 해당 리코드는 find를 통해 찾아지지 않습니다.

아래는 db_find 내의 변경사항입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2021.png)

직전에 말했듯이, logical하게 지워진 key들은 아직 tree에 남아있지만, 지워진 key들이기에 find에 서 찾아지면 안 됩니다.

따라서 logical하게 지워진 key를 search하게 될 경우, 해당 key가 없는 것으로 판단하고 NULL을 return하여 찾지 못했다는 것을 표시합니다.

이렇게 logical하개만 지운다면 결국 실제 delete는 전혀 수행되지 않기에 insert 하는 key가 늘어 날수록 delete로 키를 지우더라도 tree의 높이가 점점 증가하고, 각 노드에서 적절한 key를 찾아 다음 노드로 이동하는 search 작업역시 탐색사간이 증가하게 됩니다.

따라서 한 번씩 logical하게 지운 key 들을 실제로 delete하는 작업을 수행해야 하는데, 이를 언제 수행할지가 다음 쟁점이었습니다.

제가 선택한 방식은 insert할 때 실제 delete를 수행하는 것입니다.

아래는 제가 수정한 insert 내부입니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2022.png)

만약 leaf 노드에 어떤 key를 insert할때, 해당 노드가 최대 키 개수를 초과해 가득 차 있다면, 해 당 leaf 노드에서 여태까지 logical하게 지웠던 key들을 모두 찾고, 전부 physical하게 제거합니다.

이를 통해 logical Delete를 수행하더라도, tree modification을 줄이기 위한 logical delete로 인해서 split이라는 새로운 tree modification이 trigger 되지 않습니다.

## Test&Trade-off

제가 구현한 방식은 delete 와 insert 연산이 골고루 발생하는 상황에서 tree modification을 최소 화합니다. 하지만 이에 대한 tradeoff 역시 존재합니다.

만약 delete연산만 계속해서 발생하는 경우, tree는 실제로는 가지고 있을 필요가 없는 데이터를 계속해서 보유하게 되고, 이로 인하여 유효하지 못한 데이터 역시 탐색해야 하기에 특정 key를 find 하는 시간이 감소하며, 이는 insert와 delete 연산의 속도 자체에 영향을 주게 됩니다.

이를 확인하기 위한 test case로 아래와 같은 main 문을 수행해보았습니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2023.png)

이 test에서는 insert와 delete가 한쪽에 치우치지 않고 일정하게 발생하는 case를 확인합니다. Insert와 delete를 1만번씩 순차적으로 실행하고, 이를 10번 반복하였습니다.

제가 적용한 improvement는 delete에 비중이 지나치게 많다면 tree가 축소되지 않아 속도가 느려 지는 문제가 발생하는데, 이 test와 같이 insert 와 delete가 균등하게 반복될 경우는 insert에서 logical하게 지워진 값들을 그때 그때 physical하게 지워주기에 이런 문제없이 향상된 결과가 나오 게 됩니다.

![Untitled](README%205698876370d149a38d4f2c8a31cbbbd1/Untitled%2024.png)

위 사진은 해당 main문을 time을 통해 시간을 측정한 값입니다. 왼쪽이 기존의 on-disk b+ 트리 코드이고, 오른쪽은 제가 향상시킨 on-disk b+트리 코드입니다.

위 결과에서 알 수 있듯이, 제가 구현한 improvement는 확실히 insert와 delete가 한쪽으로 치우 치지 않는 상황에선 성능 향상이 확인되었습니다. 왼쪽은 3분 53.737초 정도로 확인되었는데 반 하여 오른쪽은 2분 17.634초로 약 1분 30초, 혹은 1.6배 정도 성능이 향상되었습니다.

하지만 만약 delete 연산만 계속해서 수행해야하는 케이스의 경우, 기존의 코드가 제가 향상시킨 코드에 비해 뛰어난 성능을 보여주는 것을 확인하며 제가 작성한 코드의 tradeoff를 체크할 수 있

었습니다.

마지막으로 제가 수행한 improvement를 요약하자면 delete애서 tree Modification이 발생할 때는 logical하게 delete하고, 이렇게 logical 하게만 지워진 값들이 insert에서 split을 야기하게 한다면 실제로 제거하는 방식의 improvement를 적용하였습니다.

## Trouble shooting

우선 제가 처음 겪었이던 trouble은 Cache memory에 관한 내용입니다. 처음엔 단순히 disk I/O자체 를 줄이는 최적의 방법이 Cache memory였기에 이를 사용하려 했지만, 이는 B+ tree에 대한 이해 도와 무관한, 별개의 방식이었이기에 새로운 방식을 적용하였습니다.

추가로 제가 겪었이던 가장 큰 문제는 바로 성능 향상에 대한 문제점이었습니다.

제가 생각한 improvement의 방식은 여러가지가 있었이지만, 어느 한쪽을 improve 시키면 반드시 그에 따른 tradeoff가 확인되어 완벽한 improvement를 찾는 것에 어려움이 있었습니다.

하지만 방법을 연구하던 중, B+ 트리라는 정체성 속에서 B+트리 자체에서 개선점을 찾는 다면, 어떠한 예외 상황 없이 모든 상황에서 더욱 뛰어난 완벽한 상위호환의 버전을 완성하는 것은 불 가능하다는 생각을 하였습니다.

이에 따라 제가 생각한 방법들의 Pros & Cons를 모두 정리하였고, 어떤 방식의 개선점이 상대적 으로 적은 risk로 큰 return을 얻을 수 있는지 생각해보며 문제를 해결할 수 있었습니다.