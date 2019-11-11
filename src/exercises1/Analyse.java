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
    private int line = 1;  //当前行数
    private String[] rwtab = {"begin","if","then","while","do","end"};
    private Word oneWord = null;

    public Analyse(String fileName){
        input = read(fileName);
        this.oneWord = new Word();
        int over =1;
        while (over<1000&&over!=-1){
            oneWord = scaner();
            //如果是注释也不输出
            if (oneWord.getTypenum()<1000 && oneWord.getTypenum()!=999){
                System.out.println("("+oneWord.getTypenum()+","+oneWord.getWord()+")");
            }
            if (oneWord.getTypenum() == -1){
                System.out.println("在第"+line+"行发生错误");
                break;
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

    public char m_getch(){
        ch = input[p_input++];
        return  ch;
    }

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

    public void lrparser(){

    }
}
