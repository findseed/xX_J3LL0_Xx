package jello.solute;

//very lazy way to keep track of JelloGrab's for the Silly Solver :zany_f
public class Cello {

    public JelloGrab cell;
    public int score;

    public Cello(JelloGrab cell, int score){
        this.cell = cell;
        this.score = score;
    }

    public Cello(int inputs, int frames, JelloGrab prev, int score){
        this.cell = new JelloGrab(inputs, frames, prev);
        this.score = score;
    }


    //?
    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        return this.cell.equals(((Cello)o).cell);
    }

    @Override
    public int hashCode(){
        return cell.getHashcode();
    }

    @Override
    public String toString(){
        return score + "-" + cell.toString();
    }


}