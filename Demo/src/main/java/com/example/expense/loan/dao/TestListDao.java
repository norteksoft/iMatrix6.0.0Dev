package com.example.expense.loan.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.example.expense.entity.TestList;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class TestListDao extends HibernateDao<TestList, Long> {
		
	public Page<TestList> list(Page<TestList> page){
		return searchPageByHql(page, "from TestList p where p.companyId=?", ContextUtils.getCompanyId());
	}
	public void batchInsertData() {
		int i = 1;
			String taskName="";
			String taskCode="";
			if(i==1){
				taskName="B_1A";
				taskCode="taskA_1A";
			}else if(i==2){
				taskName="B_1A";
				taskCode="taskA_2A";
			}else if(i==3){
				taskName="B_1A";
				taskCode="taskA_3A";
			}else if(i==4){
				taskName="B_1A";
				taskCode="taskA_4A";
			}else if(i==5){
				taskName="B_1A";
				taskCode="taskA_5A";
			}else if(i==6){
				taskName="B_2A";
				taskCode="taskA_6A";
			}else if(i==7){
				taskName="B_2A";
				taskCode="taskA_7A";
			}else if(i==8){
				taskName="B_2A";
				taskCode="taskA_8A";
			}else if(i==9){
				taskName="B_2A";
				taskCode="taskA_9A";
			}else if(i==10){
				taskName="B_2A";
				taskCode="taskA_10A";
			}else if(i==11){
				taskName="B_3A";
				taskCode="taskA_11";
			}else if(i==12){
				taskName="B_3A";
				taskCode="taskA_12";
			}else if(i==13){
				taskName="B_3A";
				taskCode="taskA_13";
			}else if(i==14){
				taskName="B_3A";
				taskCode="taskA_14";
			}else if(i==15){
				taskName="B_3A";
				taskCode="taskA_15";
			}else if(i==16){
				taskName="B_4A";
				taskCode="taskA_16";
			}else if(i==17){
				taskName="B_4A";
				taskCode="taskA_17";
			}else if(i==18){
				taskName="B_4A";
				taskCode="taskA_18";
			}else if(i==19){
				taskName="B_4A";
				taskCode="taskA_19";
			}else if(i==20){
				taskName="B_4A";
				taskCode="taskA_20";
			}else if(i==21){
				taskName="B_5A";
				taskCode="taskA_21";
			}else if(i==22){
				taskName="B_5A";
				taskCode="taskA_22";
			}else if(i==23){
				taskName="B_5A";
				taskCode="taskA_23";
			}else if(i==24){
				taskName="B_5A";
				taskCode="taskA_24";
			}else if(i==25){
				taskName="B_5A";
				taskCode="taskA_25";
			}else if(i==26){
				taskName="B_6A";
				taskCode="taskA_26";
			}else if(i==27){
				taskName="B_6A";
				taskCode="taskA_27";
			}else if(i==28){
				taskName="B_6A";
				taskCode="taskA_28";
			}else if(i==29){
				taskName="B_6A";
				taskCode="taskA_29";
			}else if(i==30){
				taskName="B_6A";
				taskCode="taskA_30";
			}else if(i==31){
				taskName="B_7A";
				taskCode="taskA_31";
			}else if(i==32){
				taskName="B_7A";
				taskCode="taskA_32";
			}else if(i==33){
				taskName="B_7A";
				taskCode="taskA_33";
			}else if(i==34){
				taskName="B_7A";
				taskCode="taskA_34";
			}else if(i==35){
				taskName="B_7A";
				taskCode="taskA_35";
			}else if(i==36){
				taskName="B_8A";
				taskCode="taskA_36";
			}else if(i==37){
				taskName="B_8A";
				taskCode="taskA_37";
			}else if(i==38){
				taskName="B_8A";
				taskCode="taskA_38";
			}else if(i==39){
				taskName="B_8A";
				taskCode="taskA_39";
			}else if(i==40){
				taskName="B_8A";
				taskCode="taskA_40";
			}else if(i==41){
				taskName="B_9A";
				taskCode="taskA_41";
			}else if(i==42){
				taskName="B_9A";
				taskCode="taskA_42";
			}else if(i==43){
				taskName="B_9A";
				taskCode="taskA_43";
			}else if(i==44){
				taskName="B_9A";
				taskCode="taskA_44";
			}else if(i==45){
				taskName="B_9A";
				taskCode="taskA_45";
			}else if(i==46){
				taskName="B_10A";
				taskCode="taskA_46";
			}else if(i==47){
				taskName="B_10A";
				taskCode="taskA_47";
			}else if(i==48){
				taskName="B_10A";
				taskCode="taskA_48";
			}else if(i==49){
				taskName="B_10A";
				taskCode="taskA_49";
			}else if(i==50){
				taskName="B_10A";
				taskCode="taskA_50";
			}else if(i==51){
				taskName="B_11";
				taskCode="taskA_51";
			}else if(i==52){
				taskName="B_11";
				taskCode="taskA_52";
			}else if(i==53){
				taskName="B_11";
				taskCode="taskA_53";
			}else if(i==54){
				taskName="B_11";
				taskCode="taskA_54";
			}else if(i==55){
				taskName="B_11";
				taskCode="taskA_55";
			}else if(i==56){
				taskName="B_12";
				taskCode="taskA_56";
			}else if(i==57){
				taskName="B_12";
				taskCode="taskA_57";
			}else if(i==58){
				taskName="B_12";
				taskCode="taskA_58";
			}else if(i==59){
				taskName="B_12";
				taskCode="taskA_59";
			}else if(i==60){
				taskName="B_12";
				taskCode="taskA_60";
			}else if(i==61){
				taskName="B_13";
				taskCode="taskA_61";
			}else if(i==62){
				taskName="B_13";
				taskCode="taskA_62";
			}else if(i==63){
				taskName="B_13";
				taskCode="taskA_63";
			}else if(i==64){
				taskName="B_13";
				taskCode="taskA_64";
			}else if(i==65){
				taskName="B_13";
				taskCode="taskA_65";
			}else if(i==66){
				taskName="B_14";
				taskCode="taskA_66";
			}else if(i==67){
				taskName="B_14";
				taskCode="taskA_67";
			}else if(i==68){
				taskName="B_14";
				taskCode="taskA_68";
			}else if(i==69){
				taskName="B_14";
				taskCode="taskA_69";
			}else if(i==70){
				taskName="B_14";
				taskCode="taskA_70";
			}else if(i==71){
				taskName="B_15";
				taskCode="taskA_71";
			}else if(i==72){
				taskName="B_15";
				taskCode="taskA_72";
			}else if(i==73){
				taskName="B_15";
				taskCode="taskA_73";
			}else if(i==74){
				taskName="B_15";
				taskCode="taskA_74";
			}else if(i==75){
				taskName="B_15";
				taskCode="taskA_75";
			}else if(i==76){
				taskName="B_16";
				taskCode="taskA_76";
			}else if(i==77){
				taskName="B_16";
				taskCode="taskA_77";
			}else if(i==78){
				taskName="B_16";
				taskCode="taskA_78";
			}else if(i==79){
				taskName="B_16";
				taskCode="taskA_79";
			}else if(i==80){
				taskName="B_16";
				taskCode="taskA_80";
			}else if(i==81){
				taskName="B_17";
				taskCode="taskA_81";
			}else if(i==82){
				taskName="B_17";
				taskCode="taskA_82";
			}else if(i==83){
				taskName="B_17";
				taskCode="taskA_83";
			}else if(i==84){
				taskName="B_17";
				taskCode="taskA_84";
			}else if(i==85){
				taskName="B_17";
				taskCode="taskA_85";
			}else if(i==86){
				taskName="B_18";
				taskCode="taskA_86";
			}else if(i==87){
				taskName="B_18";
				taskCode="taskA_87";
			}else if(i==88){
				taskName="B_18";
				taskCode="taskA_88";
			}else if(i==89){
				taskName="B_18";
				taskCode="taskA_89";
			}else if(i==90){
				taskName="B_18";
				taskCode="taskA_90";
			}else if(i==91){
				taskName="B_19";
				taskCode="taskA_91";
			}else if(i==92){
				taskName="B_19";
				taskCode="taskA_92";
			}else if(i==93){
				taskName="B_19";
				taskCode="taskA_93";
			}else if(i==94){
				taskName="B_19";
				taskCode="taskA_94";
			}else if(i==95){
				taskName="B_19";
				taskCode="taskA_95";
			}else if(i==96){
				taskName="B_20";
				taskCode="taskA_96";
			}else if(i==97){
				taskName="B_20";
				taskCode="taskA_97";
			}else if(i==98){
				taskName="B_20";
				taskCode="taskA_98";
			}else if(i==99){
				taskName="B_20";
				taskCode="taskA_99";
			}else if(i==100){
				taskName="B_20";
				taskCode="taskA_100";
			}
			for(int j = 0;j<10000;j++){
				TestList testList = new TestList(taskName, new Date(), new Date(), 5, taskCode, "开发部", "test1", 0.1f, "进行中", 5000d, new Date(), 3, new Date(), "test2", "字符串一", "字符串二","字符串三", "字符串四", "字符串五",
						"字符串六", "字符串七","字符串八", "字符串九", "字符串十","字符串十一", "字符串二","字符串三", "字符串四", "字符串五",
						1d, 2d, 3d, 4d, 5d, true, false, true, false, true, new Date(), new Date(), new Date(), new Date(), new Date(),
						1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l);
				
				testList.setDisplayOrder(j);
				this.save(testList);
			}
		}
}
//else if(i==2){
//	taskName="B_1A";
//	taskCode="taskA_2A";
//}else if(i==3){
//	taskName="B_1A";
//	taskCode="taskA_3A";
//}else if(i==4){
//	taskName="B_1A";
//	taskCode="taskA_4A";
//}else if(i==5){
//	taskName="B_1A";
//	taskCode="taskA_5A";
//}else if(i==6){
//	taskName="B_2A";
//	taskCode="taskA_6A";
//}else if(i==7){
//	taskName="B_2A";
//	taskCode="taskA_7A";
//}else if(i==8){
//	taskName="B_2A";
//	taskCode="taskA_8A";
//}else if(i==9){
//	taskName="B_2A";
//	taskCode="taskA_9A";
//}else if(i==10){
//	taskName="B_2A";
//	taskCode="taskA_10A";
//}
//else if(i==11){
//	taskName="B_3A";
//	taskCode="taskA_11";
//}else if(i==12){
//	taskName="B_3A";
//	taskCode="taskA_12";
//}else if(i==13){
//	taskName="B_3A";
//	taskCode="taskA_13";
//}else if(i==14){
//	taskName="B_3A";
//	taskCode="taskA_14";
//}else if(i==15){
//	taskName="B_3A";
//	taskCode="taskA_15";
//}else if(i==16){
//	taskName="B_4A";
//	taskCode="taskA_16";
//}else if(i==17){
//	taskName="B_4A";
//	taskCode="taskA_17";
//}else if(i==18){
//	taskName="B_4A";
//	taskCode="taskA_18";
//}else if(i==19){
//	taskName="B_4A";
//	taskCode="taskA_19";
//}else if(i==20){
//	taskName="B_4A";
//	taskCode="taskA_20";
//}else if(i==21){
//	taskName="B_5A";
//	taskCode="taskA_21";
//}else if(i==22){
//	taskName="B_5A";
//	taskCode="taskA_22";
//}else if(i==23){
//	taskName="B_5A";
//	taskCode="taskA_23";
//}else if(i==24){
//	taskName="B_5A";
//	taskCode="taskA_24";
//}else if(i==25){
//	taskName="B_5A";
//	taskCode="taskA_25";
//}else if(i==26){
//	taskName="B_6A";
//	taskCode="taskA_26";
//}else if(i==27){
//	taskName="B_6A";
//	taskCode="taskA_27";
//}else if(i==28){
//	taskName="B_6A";
//	taskCode="taskA_28";
//}else if(i==29){
//	taskName="B_6A";
//	taskCode="taskA_29";
//}else if(i==30){
//	taskName="B_6A";
//	taskCode="taskA_30";
//}else if(i==31){
//	taskName="B_7A";
//	taskCode="taskA_31";
//}else if(i==32){
//	taskName="B_7A";
//	taskCode="taskA_32";
//}else if(i==33){
//	taskName="B_7A";
//	taskCode="taskA_33";
//}else if(i==34){
//	taskName="B_7A";
//	taskCode="taskA_34";
//}else if(i==35){
//	taskName="B_7A";
//	taskCode="taskA_35";
//}else if(i==36){
//	taskName="B_8A";
//	taskCode="taskA_36";
//}else if(i==37){
//	taskName="B_8A";
//	taskCode="taskA_37";
//}else if(i==38){
//	taskName="B_8A";
//	taskCode="taskA_38";
//}else if(i==39){
//	taskName="B_8A";
//	taskCode="taskA_39";
//}else if(i==40){
//	taskName="B_8A";
//	taskCode="taskA_40";
//}else if(i==41){
//	taskName="B_9A";
//	taskCode="taskA_41";
//}else if(i==42){
//	taskName="B_9A";
//	taskCode="taskA_42";
//}else if(i==43){
//	taskName="B_9A";
//	taskCode="taskA_43";
//}else if(i==44){
//	taskName="B_9A";
//	taskCode="taskA_44";
//}else if(i==45){
//	taskName="B_9A";
//	taskCode="taskA_45";
//}else if(i==46){
//	taskName="B_10A";
//	taskCode="taskA_46";
//}else if(i==47){
//	taskName="B_10A";
//	taskCode="taskA_47";
//}else if(i==48){
//	taskName="B_10A";
//	taskCode="taskA_48";
//}else if(i==49){
//	taskName="B_10A";
//	taskCode="taskA_49";
//}else if(i==50){
//	taskName="B_10A";
//	taskCode="taskA_50";
//}else if(i==51){
//	taskName="B_11";
//	taskCode="taskA_51";
//}else if(i==52){
//	taskName="B_11";
//	taskCode="taskA_52";
//}else if(i==53){
//	taskName="B_11";
//	taskCode="taskA_53";
//}else if(i==54){
//	taskName="B_11";
//	taskCode="taskA_54";
//}else if(i==55){
//	taskName="B_11";
//	taskCode="taskA_55";
//}else if(i==56){
//	taskName="B_12";
//	taskCode="taskA_56";
//}else if(i==57){
//	taskName="B_12";
//	taskCode="taskA_57";
//}else if(i==58){
//	taskName="B_12";
//	taskCode="taskA_58";
//}else if(i==59){
//	taskName="B_12";
//	taskCode="taskA_59";
//}else if(i==60){
//	taskName="B_12";
//	taskCode="taskA_60";
//}else if(i==61){
//	taskName="B_13";
//	taskCode="taskA_61";
//}else if(i==62){
//	taskName="B_13";
//	taskCode="taskA_62";
//}else if(i==63){
//	taskName="B_13";
//	taskCode="taskA_63";
//}else if(i==64){
//	taskName="B_13";
//	taskCode="taskA_64";
//}else if(i==65){
//	taskName="B_13";
//	taskCode="taskA_65";
//}else if(i==66){
//	taskName="B_14";
//	taskCode="taskA_66";
//}else if(i==67){
//	taskName="B_14";
//	taskCode="taskA_67";
//}else if(i==68){
//	taskName="B_14";
//	taskCode="taskA_68";
//}else if(i==69){
//	taskName="B_14";
//	taskCode="taskA_69";
//}else if(i==70){
//	taskName="B_14";
//	taskCode="taskA_70";
//}else if(i==71){
//	taskName="B_15";
//	taskCode="taskA_71";
//}else if(i==72){
//	taskName="B_15";
//	taskCode="taskA_72";
//}else if(i==73){
//	taskName="B_15";
//	taskCode="taskA_73";
//}else if(i==74){
//	taskName="B_15";
//	taskCode="taskA_74";
//}else if(i==75){
//	taskName="B_15";
//	taskCode="taskA_75";
//}else if(i==76){
//	taskName="B_16";
//	taskCode="taskA_76";
//}else if(i==77){
//	taskName="B_16";
//	taskCode="taskA_77";
//}else if(i==78){
//	taskName="B_16";
//	taskCode="taskA_78";
//}else if(i==79){
//	taskName="B_16";
//	taskCode="taskA_79";
//}else if(i==80){
//	taskName="B_16";
//	taskCode="taskA_80";
//}else if(i==81){
//	taskName="B_17";
//	taskCode="taskA_81";
//}else if(i==82){
//	taskName="B_17";
//	taskCode="taskA_82";
//}else if(i==83){
//	taskName="B_17";
//	taskCode="taskA_83";
//}else if(i==84){
//	taskName="B_17";
//	taskCode="taskA_84";
//}else if(i==85){
//	taskName="B_17";
//	taskCode="taskA_85";
//}else if(i==86){
//	taskName="B_18";
//	taskCode="taskA_86";
//}else if(i==87){
//	taskName="B_18";
//	taskCode="taskA_87";
//}else if(i==88){
//	taskName="B_18";
//	taskCode="taskA_88";
//}else if(i==89){
//	taskName="B_18";
//	taskCode="taskA_89";
//}else if(i==90){
//	taskName="B_18";
//	taskCode="taskA_90";
//}else if(i==91){
//	taskName="B_19";
//	taskCode="taskA_91";
//}else if(i==92){
//	taskName="B_19";
//	taskCode="taskA_92";
//}else if(i==93){
//	taskName="B_19";
//	taskCode="taskA_93";
//}else if(i==94){
//	taskName="B_19";
//	taskCode="taskA_94";
//}else if(i==95){
//	taskName="B_19";
//	taskCode="taskA_95";
//}else if(i==96){
//	taskName="B_20";
//	taskCode="taskA_96";
//}else if(i==97){
//	taskName="B_20";
//	taskCode="taskA_97";
//}else if(i==98){
//	taskName="B_20";
//	taskCode="taskA_98";
//}else if(i==99){
//	taskName="B_20";
//	taskCode="taskA_99";
//}else if(i==100){
//	taskName="B_20";
//	taskCode="taskA_100";
//}