package exercises1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Analyse {
    private char[] input = null;//存放输入的数据
    private String token="";//存放扫描出来的单词
  //  private  int p_token = 0;
    private  int p_input = 0;//输入数组的下标
    private  char ch = '\0'; //当前读入字符
    private int line = 1;  //当前行数，当ch读到\n时会加1
    private String[] rwtab = {"begin","if","then","while","do","end"};
    private Word oneWord = null;
    private int errorNumber = 0;//记录错误的个数，包括了词法错误和语法错误

    public Analyse(String fileName){
       //cifa(fileName);
        input = read(fileName);
        this.oneWord = new Word();
        oneWord=scaner();
        lrparser();
        System.out.println("编译完成，错误次数："+errorNumber);
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
                        temp.setTypenum(999);
                        temp.setWord("//");
                        while (ch!='\n')
                            m_getch();
                         retract();
                         return temp;
                    }
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
        for (int i=0;i<6; i++){
            if (rwtab[i].equals(token))
                return i+1;//关键字表格中有匹配的，就返回该关键字种别码
        }
        return 10;
    }

    //语法分析，只能针对赋值语句
    public void lrparser(){
        //读入的第一个词不是begin就输出错误，然后接着执行
        if (oneWord.getTypenum() != 1)
            printError("发生begin错误！");
        oneWord=scaner();
        yucu();
        if (oneWord.getTypenum()!=6)
            printError("发生end错误！");
        oneWord=scaner();
        if (oneWord.getTypenum()!=1000)
            printError("发生结束符错误！");
    }

    public void yucu(){
        statement();
        while (oneWord.getTypenum()==34){
            oneWord=scaner();
            statement();
        }
    }

    /**
     * 赋值语句终结符
     */
    public void statement(){
        if (oneWord.getTypenum()!=10){
            printError("发生语句错误！");
            //发生错误时，此行不需再分析
           // return;
        }
        oneWord=scaner();
        if (oneWord.getTypenum()!=21){
            printError("发生赋值号错误！");
           // return;
        }
        oneWord=scaner();
        expression();
    }

    public void expression(){
        term();
        while (oneWord.getTypenum()==22||oneWord.getTypenum()==23){
           oneWord=scaner();
            term();
        }
    }

    public void term(){
        factor();
        while (oneWord.getTypenum()==24||oneWord.getTypenum()==25){
            oneWord=scaner();
            factor();
        }
    }

    public void factor(){
        if (oneWord.getTypenum()==10||oneWord.getTypenum()==11)
            oneWord=scaner();
        else{
            if (oneWord.getTypenum()!= 26)
                printError("发生表达式错误！");
            oneWord=scaner();
            expression();
            if (oneWord.getTypenum()!= 27)
                printError("发生')'错误！");
            oneWord=scaner();
        }

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




}
