package Memoria;

public class MemHD {

    private Integer[] memoriaHD;
    private final int tamanhoHD = 55;

    public MemHD() {
        memoriaHD = new Integer[tamanhoHD];
    }

    public Integer getValorHD(int indice) {
        return this.memoriaHD[indice];
    }

    public void setValorHD(int indice, Integer valor) {
        this.memoriaHD[indice] = valor;
    }

    public Integer getIndiceLivreHD() {

        for (int i = 0; i < memoriaHD.length; i++) {
            if (memoriaHD[i] == null) {
                System.out.println("Conseguiu espaco no HD, espaco: 0y" + i);
                return i;
            }
        }
        System.out.println("NAO conseguiu espaco no HD");
        return null;
    }

    public void mostrarTudoHD() {

        System.out.println("/----TUDO DO HD---/");

        for (int i = 0; i < this.memoriaHD.length; i++) {

            if (memoriaHD[i] == null) {
                continue;
            }

            System.out.println("posicao: " + i + " = " + memoriaHD[i]);

        }
    }

}
