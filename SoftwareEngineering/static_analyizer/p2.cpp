#include <map>
#include<iostream>
using namespace std;
class DivZeroCheckerTestClass {
private:
    std::map<int, int> map;
    int num = 0;

public:
    DivZeroCheckerTestClass(int num) {
        this->num = num;
    }

    int func(int a) {
        if (a == num)
            return 0;

        map.clear();//this cause false positive!

        if (a != num)
            return -1;
        return 10 / (a - num);
    }
    int chk() { return num;}
    
};
int main(){
	DivZeroCheckerTestClass a=  DivZeroCheckerTestClass(5);
	a.func(4);
	cout<<a.chk()<<endl;
//	cout<<a.chk2()<<endl;


}
