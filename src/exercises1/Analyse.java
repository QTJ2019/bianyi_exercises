package exercises1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analyse {
    private char[] input = null;//存放输入的数据
    private String token="";//存放扫描出来的单词
  //  private  int p_token = 0;
    private  int p_input = 0;//输入数组的下标
    private  char ch = '\0'; //当前读入字符
    private int line = 1;  //当前行数，当ch读到\n时会加1
    private String[] rwtab = {"begin","if","then","while","do","end","endif","endwhl"};
    private Word oneWord = null;
    private int errorNumber = 0;//记录错误的个数，包括了词法错误和语法错误
    private int k=1;//该变量在newtemp中使用
    private List<Quadruplet> quadlist = new ArrayList();

    public Analyse(String fileName){
       //cifa(fileName);
        input = read(fileName);
        this.oneWord = new Word();
        oneWord=scaner();
        lrparser();
        System.out.println("编译完成，错误次数："+errorNumber);
        int i =0;
        for (Quadruplet each : quadlist){
            if(each.getResult().length()>2){
                System.out.println((i++)+" "+each.getResult()+" "+each.getAg1()+" "+each.getOp()+" "+each.getArg2());
            }
            else
            System.out.println((i++) +" "+each.getResult()+"="+each.getAg1()+each.getOp()+each.getArg2());
        }

    }

    public void cifa(String fileName){
        input = read(fileName);
        this.oneWord = new Word();
        int over =1;
        while (over<1000){
            oneWord = scaner();
            //如果是注释也不输出
            if (oneWord.getTypenum()<1000 && oneWord.getTypenum()!=999){
                System.out.println("("+oneWord.getTypenum()+","+oneWord.getWord()+")");
            }
            if (oneWord.getTypenum() == -1){
                printError("");
            }
            over = oneWord.getTypenum();
        }
        System.out.println("程序结束！");
    }
    public  char[] read(String fileName){
        String temp = "";
        String context = "";
        try{
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            while ((temp = br.readLine()) !=null){
                context += temp;
                context +='\n';//使用readLine读取行时，它不会读入换行符，要自行添加
            }

        }catch (FileNotFoundException e ){
            e.printStackTrace();
        }catch (IOException e1){
            e1.printStackTrace();
        }
       // System.out.println(context);
        context+='\0';  //在输入字符串末尾加一个空字符
        return input = context.toCharArray();
    }

    /**
     * 扫描获得一个词和它对应的种别码，
     * @return 返回一个Word对象，记录了一个词和对应的种别码
     */
    public  Word scaner() {
        if (input == null)
        {
            System.out.println("还没读入数据");
            return null;
        }
        Word temp = new Word();
        temp.setTypenum(10);
        temp.setWord("");
        token = "";
        m_getch();
        getbc();
        if (letter()){
            while (letter() || digit()){
                concat();
                m_getch();
            }
            retract();
            temp.setTypenum(reserve());
            temp.setWord(token);
        }
        else if (digit()){
            while (digit()){
                concat();
                m_getch();
            }
            retract();
            temp.setTypenum(11);
            temp.setWord(token);
        }
        else switch (ch){
                case '=':m_getch();
                          if (ch == '='){
                              temp.setWord("==");
                              temp.setTypenum(39);
                              return temp;
                          }
                          retract();
                          temp.setWord("=");
                          temp.setTypenum(21);
                          return temp;
                case '+':
                    temp.setTypenum(22);
                    temp.setWord("+");
                    return temp;
                case '-':
                    temp.setTypenum(23);
                    temp.setWord("-");
                    return temp;
                case '*':
                    temp.setWord("*");
                    temp.setTypenum(24);
                    return temp;
                case '/':
                    m_getch();
                    if (ch == '/'){
                        //如果是注释，需要跳过注释，再读一个词
                        temp.setTypenum(999);
                        temp.setWord("//");
                        while (ch!='\n')
                            m_getch();
                         retract();
                         temp=scaner();
                         return temp;
                    }
                    retract();
                    temp.setTypenum(25);
                    temp.setWord("/");
                    return temp;
                case '(':
                    temp.setWord("(");
                    temp.setTypenum(26);
                    return temp;
                case ')':
                    temp.setTypenum(27);
                    temp.setWord(")");
                    return temp;
                case '[':
                    temp.setWord("{");
                    temp.setTypenum(28);
                    return temp;
                case ']':
                    temp.setTypenum(29);
                    temp.setWord("]");
                    return temp;
                case '{':
                    temp.setWord("{");
                    temp.setTypenum(30);
                    return temp;
                case '}':
                    temp.setTypenum(31);
                    temp.setWord("}");
                    return temp;
                case ',':
                    temp.setWord(",");
                    temp.setTypenum(32);
                    return temp;
                case ':':
                    temp.setTypenum(33);
                    temp.setWord(":");
                    return temp;
                case ';':
                    temp.setWord(";");
                    temp.setTypenum(34);
                    return temp;
                case '>':
                    m_getch();
                    if (ch == '='){
                        temp.setWord(">=");
                        temp.setTypenum(37);
                        return temp;
                    }
                    retract();
                    temp.setTypenum(35);
                    temp.setWord(">");
                    return temp;
                case '<':
                    m_getch();
                    if (ch=='='){
                        temp.setTypenum(38);
                        temp.setWord("<=");
                        return temp;
                    }
                    retract();
                    temp.setWord("<");
                    temp.setTypenum(36);
                    return temp;
                case '!':
                    if (ch == '='){
                        temp.setTypenum(40);
                        temp.setWord("!=");
                        return temp;
                    }
                    retract();
                    temp.setWord("ERROR");
                    temp.setTypenum(-1);
                    return temp;
                case '\0':
                    temp.setTypenum(1000);
                    temp.setWord("OVER");
                    return temp;
                 default:
                     temp.setWord("ERROR");
                     temp.setTypenum(-1);
                     return temp;
            }
        return temp;
    }

    /*
    从缓冲区读取一个字符到ch中,如果p_input大于或等于input的长度则，ch只能是input最后的那个元素
     */
    public char m_getch(){
        if (p_input<input.length)
             ch = input[p_input++];
        return  ch;
    }

    //去掉空白符号
    public void getbc(){
        while (ch ==' '||ch == '\n'){
            if (ch == '\n')
                line++;
           ch = input[p_input];
           p_input++;
        }
    }

    public boolean letter(){
        if (ch >='a'&&ch<='z'||ch>='A'&&ch<='z')
            return true;
        else return false;
    }

    public boolean digit(){
        if (ch>='0'&&ch<='9')
            return true;
        else return false;
    }

    public void concat(){
        token += ch;
    }

    public void retract(){
        p_input = p_input -1;
    }

    public int reserve(){
        for (int i=0;i<8; i++){
            if (rwtab[i].equals(token))
                return i+1;//关键字表格中有匹配的，就返回该关键字种别码
        }
        return 10;//没有匹配到就返回10
    }

    //语法分析，只能针对赋值语句
    public int lrparser(){
        int schain = 0;
        //读入的第一个词不是begin就输出错误，然后接着执行
        if (oneWord.getTypenum() != 1)
            printError("发生begin错误！");
        oneWord=scaner();
        schain = yucu();
        if (oneWord.getTypenum()!=6)
            printError("发生end错误！");
        oneWord=scaner();
        if (oneWord.getTypenum()!=1000)
            printError("发生结束符错误！");
        return schain;
    }

    public int yucu(){
        int schain = 0;
        schain = statement();
        while (oneWord.getTypenum()==34){
            oneWord=scaner();
            schain = statement();
        }
//        if (oneWord.getTypenum()!=34)
//            printError("发生；错误！");
        return schain;
    }

    /**
     * 赋值语句终结符
     */
    public int statement(){
        int schain =0;
        String tt = null;
        String eplace = null;
        if (oneWord.getTypenum()==10){
            //printError("发生语句错误！");
            //发生错误时，此行不需再分析
           // return;
        tt = oneWord.getWord();
        oneWord=scaner();
        if (oneWord.getTypenum()!=21){
            printError("发生赋值号错误！");
           // return;
        }
        oneWord=scaner();
        eplace = expression();
        emit(tt,eplace,"","");
        schain = 0;
    }else if (oneWord.getWord().equals("if")){
            oneWord = scaner();
            schain =ifyuju();
        }else if (oneWord.getWord().equals("while")){
            oneWord = scaner();
            schain = whlyuju();
        }
        //这里是不是应该设一个报错
        return schain;

    }

    /**
     * 表达式语句，<表达式>::=<项>｛+<项>|-<项>｝
     * flag 标识该表达式是否在赋值语句，true表示在，false表示不在
     * @return 返回表达式的结果
     */
    public String expression(){
        String tp = null;
        String ep2 = null;
        String eplace = null;
        String tt = null;
        eplace = term();
        while (oneWord.getTypenum()==22||oneWord.getTypenum()==23){
            tt = oneWord.getWord();//操作符tt='+'或者'-'
           oneWord=scaner();
            ep2 =term();
            tp = newtemp();
            emit(tp,eplace,tt,ep2);//生成四元式送入四元式表
            eplace = tp;
        }
        return eplace;
    }

    public String term(){
        String tp = null;
        String ep2 = null;
        String eplace = null;
        String tt = null;
        eplace=factor();
        while (oneWord.getTypenum()==24||oneWord.getTypenum()==25){
            tt = oneWord.getWord();
            oneWord=scaner();
            ep2 = factor();
            tp = newtemp();
            emit(tp,eplace,tt,ep2);
            eplace = tp;
        }
        return eplace;
    }

    public String factor(){
        String fplace = null;
        if (oneWord.getTypenum()==10||oneWord.getTypenum()==11){
            fplace = oneWord.getWord();
            oneWord=scaner();
        }

        else{
            if (oneWord.getTypenum()!= 26)
                printError("发生表达式错误！");
            oneWord=scaner();
            fplace =expression();
            if (oneWord.getTypenum()!= 27)
                printError("发生')'错误！");
            oneWord=scaner();
        }
        return fplace;
    }

    /**
     * if语句，
     * @return
     */
    public int ifyuju(){
        int schain = 0;
        Quadruplet temp = null;
            temp =conditon();
            //不等于then,报错
            if (oneWord.getTypenum()!=3){
                printError("发生缺少then错误！");
                //最好跳过整个if语句
            }
            oneWord = scaner();
            yucu();
            temp.setResult(temp.getResult()+quadlist.size());//then后面要有语句，否则会跳转出错
            if (!oneWord.getWord().equals("endif")){
                printError("缺少endif！");
            }
            oneWord = scaner();
        return  schain;
    }

    public int whlyuju(){
        int schain =0;
        Quadruplet temp = null;
        temp = conditon();
        if (oneWord.getTypenum()!=3){
            printError("发生缺少then错误！");
        }
        oneWord = scaner();
        yucu();
        temp.setResult(temp.getResult()+quadlist.size());
        if (!oneWord.getWord().equals("endwhl")){
            printError("缺少endwhl！");
        }
        oneWord = scaner();
        return  schain;
    }

    public Quadruplet conditon(){
        int schain = 0;
        String eplace1 = null;//存放expression（）的返回值
        String eplace2 = null;
        String tempWord = null;
        eplace1 = expression();
        Quadruplet temp = null;//返回四元组对象
        switch (oneWord.getWord()){
            case"<":break;
            case"<=":break;
            case">":break;
            case">=":break;
            case"==":break;
            case "!=":break;
            default:printError("出现条件语句错误！");
            //还需要跳过整个赋值语句
            return null;
        }
        tempWord = oneWord.getWord();
        oneWord = scaner();
        eplace2 = expression();
        emit("true"+(quadlist.size()+2),eplace1,tempWord,eplace2);//生成条件语句,如果then后面为空，会导致跳转异常
        temp = new Quadruplet("false",eplace1,tempWord,eplace2);//该对象的跳转行数需要在ifyuju（）中添加
        quadlist.add(temp);//
        return temp;
    }
    /**
     * 输出错误的行数，并且把输入数组的下标定位到最近的一个换行符的位置,且令errorNumber加1
     */
    public void printError(String content){
        errorNumber++;
        System.out.println("第"+line+"行"+content);
        //将下标定位到最近换行符
        while (ch!='\n')
            m_getch();
        //要回退一次
        retract();
    }

    /**
     * 该函数会返回一个新的临时变量名，临时变量名产生的顺序为T1，T2，···
     * @return
     */
    public String newtemp(){
        return "t"+k++;
    }

    public void emit(String result, String arg1, String op, String arg2){
        quadlist.add(new Quadruplet(result,arg1,op,arg2));
    }


}
