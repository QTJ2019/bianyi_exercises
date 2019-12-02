package exercises1;

public class Quadruplet {
    private String result = null;
    private String ag1 = null;
    private String op = null;
    private String arg2 = null;

    public Quadruplet(String result, String ag1, String op, String arg2){
        this.result = result;
        this.ag1 = ag1;
        this.op = op;
        this.arg2 = arg2;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAg1() {
        return ag1;
    }

    public void setAg1(String ag1) {
        this.ag1 = ag1;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }
}
