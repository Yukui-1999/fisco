pragma solidity >=0.4.24 <0.6.11;

import "./Table.sol";

contract ZS01 {
    // event
    event RegisterEvent(int256 ret, string indexed id_time,string indexed total_hash,string indexed part_hash,int256 result);
    
    
    constructor() public {
        // 构造函数中创建t_asset表
        createTable();
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001); 
        // 管理表, key : id_time, field : total_hash,part_hash,result
       
        // 创建表
        tf.createTable("ZStable", "id_time", "total_hash,part_hash,result");
    }

    function openTable() internal returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("ZStable");
        return table;
    }

    /*
    描述 : 根据id_time查询total_hash part_hash result
    参数 ： 
            id_time:传感器id及时间区间

    返回值：
            参数一： 成功返回0, 主键不存在返回-1
            参数二： 第一个参数为0时有效，total_hash part_hash result
    */
    function select(string memory id_time) public view returns(int256,string,string,int256) {
        // 打开表
        Table table = openTable();
        // 查询
        Entries entries = table.select(id_time, table.newCondition());
        string memory total_hash = "";
        string memory part_hash="";
        int256 result=-2;
        if (0 == uint256(entries.size())) {
            return (-1,total_hash,part_hash, result);
        } else {
            Entry entry = entries.get(0);
            return (0, entry.getString("total_hash"),entry.getString("part_hash"),int256(entry.getInt("result")));
        }
    }

    /*
    描述 : 数据注册
    参数 ： 
            id_time : 传感器id及时间区间
            total_hash  : 全体数据hash
            part_hash:用于推断的数据hash
            result:判定结果
    返回值：
            0  注册成功
            -1 已存在
            -2 其他错误
    */
    function register(string memory id_time, string total_hash,string part_hash,int256 result) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        string memory temp_total;
        string memory temp_part;
        int256 temp_result = 0;
        // 查询主键是否存在
        (ret, temp_total,temp_part,temp_result) = select(id_time);
        if(ret != 0) {
            Table table = openTable();
            
            Entry entry = table.newEntry();
            entry.set("id_time", id_time);
            entry.set("total_hash",total_hash);
            entry.set("part_hash",part_hash);
            entry.set("reuslt",result);
            // 插入
            int count = table.insert(id_time, entry);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = -2;
            }
        } else {
            // 账户已存在
            ret_code = -1;
        }

        emit RegisterEvent(ret_code, id_time, total_hash,part_hash,result);

        return ret_code;
    }

    
}
