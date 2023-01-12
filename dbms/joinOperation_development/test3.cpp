#include <bits/stdc++.h>
using namespace std;

class name_grade {
	public:
		string student_name;
		int korean;
		int math;
		int english;
		int science;
		int social;
		int history;

		void set_grade(string tuple)
		{
			stringstream tuplestr(tuple);
			string tempstr;

			getline(tuplestr, student_name, ',');

			getline(tuplestr, tempstr, ',');
			korean = stoi(tempstr);
			
			getline(tuplestr, tempstr, ',');
			math = stoi(tempstr);
			
			getline(tuplestr, tempstr, ',');
			english = stoi(tempstr);
			
			getline(tuplestr, tempstr, ',');
			science = stoi(tempstr);
			
			getline(tuplestr, tempstr, ',');
			social = stoi(tempstr);
			
			getline(tuplestr, tempstr);
			history = stoi(tempstr);
		}
};

class name_number{
	public :
		string student_name;
		string student_number;

		void set_number(string tuple)
		{
			stringstream tuplestr(tuple);
			string tempstr;


			getline(tuplestr, student_name, ',');
			getline(tuplestr, student_number, ',');
		}
};

string make_tuple(string name, string number)
{
	string ret = "";

	ret += name+ "," + number +"\n";

	return ret;
}

int main() {

	string buffer[2];
	name_grade temp0;
	name_grade temp1;
	name_number temp2;
	fstream block[12];
	ofstream output;

	output.open("./output3.csv");

	if (output.fail())
	{
		cout << "output file opening fail.\n";
	}

	/*********************************************************************/

	temp0 = name_grade();
	temp1 = name_grade();
	temp2 = name_number();
 	int out_cnt=0;
	int in_cnt=0;
	int inin_cnt=0;
	for (int i = 0; i < 1000; i++) {
		
			
			// cout<<"outer block change!"<<endl; 
			string line;
			
			string f = "./name_grade1/";
			string b = ".csv";
			string ss;
			ss += f;
			ss += to_string(i);
			ss += b;
			block[0].open(ss);
			while (!block[0].eof()) {
				getline(block[0], buffer[0]);
				if (block[0].eof()) continue;
		//		cout<<"outer: "<<buffer[0]<<endl;
				out_cnt++;
				temp0.set_grade(buffer[0]);


				for (int j = 0; j < 1000; j++) {


					string ff = "./name_grade2/";
					string bb = ".csv";
					string ssss;
					ssss += ff;
					ssss += to_string(j);
					ssss += bb;
					block[1].open(ssss);

					if (block[0].is_open()) {

						while (!block[1].eof()) {
							getline(block[1], buffer[0]);
							if (block[1].eof()) continue;
						//	cout<<"inner: "<<buffer[0]<<endl;
							in_cnt++;
							temp1.set_grade(buffer[0]);

							if (temp0.student_name.compare(temp1.student_name) == 0) {//nat join two tuple
								int cnt = 0;
							//	cout<<"find: "<<temp0.student_name<<" "<<temp1.student_name<<endl;
								if (temp0.korean > temp1.korean) cnt++;
								if (temp0.math > temp1.math) cnt++;
								if (temp0.english > temp1.english) cnt++;
								if (temp0.science > temp1.science) cnt++;
								if (temp0.social > temp1.social) cnt++;
								if (temp0.history > temp1.history)cnt++;
								if (cnt >= 2) {
									
									for (int k = 0; k <1000; k++) {
										
										string f3 = "./name_number/";
										string b3 = ".csv";
										string path3;
										path3 += f3;
										path3 += to_string(k);
										path3 += b3;
										block[2].open(path3);
										if (block[2].is_open()) {
											while (!block[2].eof()) {
												getline(block[2], buffer[0]);
												if (block[2].eof()) continue;
												temp2.set_number(buffer[0]);
												if (temp2.student_name.compare(temp1.student_name) == 0) {
													string improve = make_tuple(temp2.student_name, temp2.student_number);//add to join table and continue
													output << improve;
												//	cout<<"get: "<<i<<" "<<j<<" "<<k<<endl;
                                                // cout<<"buf: "<<buffer[0]<<endl;
                                                // cout<< "0: "<<temp0.student_name<<temp0.korean<<" "<<temp0.math<<" "<<temp0.english<<" "<<temp0.science<<" "<<temp0.social<<" "<<temp0.history<<endl;
                                                // cout<< "1: "<<temp1.student_name<<temp1.korean<<" "<<temp1.math<<" "<<temp1.english<<" "<<temp1.science<<" "<<temp1.social<<" "<<temp1.history <<endl;
                                                // cout<<endl;
                                                // inin_cnt++;

												}
											}
										}
										block[2].close();
									}

								}


							}


						}


					}
					block[1].close();
				}
			}
		block[0].close();
}







	// write codes here.








//	cout<<"out_cnt:  "<<out_cnt<<" in_cnt: "<<in_cnt<<" inin_cnt: "<<inin_cnt<<endl;



	/*********************************************************************/


	output.close();

	
}
