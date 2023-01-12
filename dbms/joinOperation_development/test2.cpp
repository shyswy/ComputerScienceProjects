#include <bits/stdc++.h>
using namespace std;

class name_age {
	public:
		string name;
		string age;
		
		void set_name_age(string tuple)
		{
			stringstream tuplestr(tuple);
			string agestr;

			getline(tuplestr, name, ',');
			getline(tuplestr, age);
		}
};

class name_salary {
	public:
		string name;
		string salary;
		
		void set_name_salary(string tuple)
		{
			stringstream tuplestr(tuple);
			string salarystr;

			getline(tuplestr, name, ',');
			getline(tuplestr, salary);
		}
};

string make_tuple(string name, string age, string salary)
{
	return name+ ',' + age + ',' + salary + '\n';
}

string make_double(string name, string val) {

	return name+ ',' + val+ '\n';

}
int hash_func(string name) {
	int val = 0;
	for (int i = 0; i < name.length(); i++) {
		val += name[i];
	}
	val = val % 10;
	val += 2;// val은 2~ 11 ( buffer 내부} 가 나온다.
	return val;
}


int main(){

	string buffer[2];
	name_age temp0;
	name_salary temp1;
	fstream block[12];
	ofstream output;

	output.open("./output2.csv");

	if(output.fail())
	{
		cout << "output file opening fail.\n";
	}

	temp0 = name_age();
	temp1 = name_salary();
	
	for (int i = 2; i < 12; i++) {
		string f = "./hash";
		string b = ".csv";
		string ss;
		ss += f;
		ss += to_string(i);
		ss += b;
		block[i].open(ss,ios_base::out);
		if(block[i].fail()){
          cout << "output1 file opening fail.\n";
      }
//		block[i].close();
		
	}
	


	for (int i = 0; i < 1000; i++) {
		string line;
		char num = '0';
		string f = "./name_age/";
		string b = ".csv";
		string ss;
		ss += f;
		ss += to_string(i);
		ss += b;
		block[0].open(ss);
		while (!block[0].eof()) {
			
				getline(block[0], buffer[0]);
				if (block[0].eof()) continue;
				temp0.set_name_age(buffer[0]);
				
				int hash_val = hash_func(temp0.name);
/*				 string f = "./hash";
          string b = ".csv";
          string ss;
          ss += f;
          ss += to_string(hash_val);
          ss += b;
          block[hash_val].open(ss,ios_base::out);*/

		//		cout<<hash_val<<endl;
				string tuple=make_double(temp0.name, temp0.age);
//				cout<<"name: "<<temp0.name<<" hsah: "<<hash_val<<endl;
				if(!block[hash_val].is_open()) cout<<"not open!"<<endl;
				block[hash_val] << tuple;
		//		cout<<tuple<<endl;
		}
		block[0].close();
	}

	for (int i = 2; i < 12; i++) block[i].close();
//cout<<"mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"<<endl;
int cnt=0;
	for (int i = 0; i < 1000; i++) {
		string line;
		
		string f = "./name_salary/";
		string b = ".csv";
		string ss;
		ss += f;
		ss += to_string(i);
		ss += b;
		block[1].open(ss);
		if(!block[1].is_open()){
			cout<<"open fail!"<<endl;
			continue;}
		while (!block[1].eof()) {
			
			getline(block[1], buffer[0]);
			if (block[1].eof()) continue;
			//cout<<buffer[0]<<endl;
			temp1.set_name_salary(buffer[0]);
			int hash_val = hash_func(temp1.name);
			string f1 = "./hash";
			string b1 = ".csv";
			string ss1;
			ss1 += f1;
			ss1 += to_string(hash_val);
			ss1 += b1;
	//		cout<<ss1<<endl;
			block[hash_val].open(ss1,ios_base::in);
			if(!block[hash_val].is_open()){
		//		cout<<"cant open"<<endl;
				continue;}
		
			while (!block[hash_val].eof()) {
				if (block[hash_val].eof()) continue;
				getline(block[hash_val], buffer[0]);
//				cout<<"buf: "<<buffer[0]<<" hash val: "<<hash_val<<endl; 
				temp0.set_name_age(buffer[0]);
	//			cout<<temp0.name<<" "<<temp1.name<<endl;
				if(temp0.name.compare(temp1.name)==0){
					string ans=make_tuple(temp0.name, temp0.age, temp1.salary);
					output << ans;
//					cnt++;
				}
					
			}
			block[hash_val].close();
			
			
			
			
		}
		block[1].close();

	}

//	cout<<cnt<<endl;
	

	block[0].close();
	block[1].close();



	/******************************************************************/
	













	
	// write codes here.















	/******************************************************************/

	output.close();

	
}
