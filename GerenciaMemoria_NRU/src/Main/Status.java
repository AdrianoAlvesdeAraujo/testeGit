package Main;

import Memoria.MemHD;
import Memoria.MemRam;
import Memoria.MemVirtual;

public class Status {

    private MemRam MenFisica;
    private MemVirtual MV;
    private MemHD HD;
    private int contador = 0;
    int QUANTIDADE_THREADS = 2;

    public Status(MemRam MemFisica_, MemVirtual MV_, MemHD HD_) {
        this.MenFisica = MemFisica_;
        this.MV = MV_;
        this.HD = HD_;
    }

    public void status_fim() {
        this.contador++;
        if (contador == QUANTIDADE_THREADS) {
            System.out.println("Status Final:");
            MV.mostrarTudoMenVirutal();
            MenFisica.mostrarTudoRam();
            HD.mostrarTudoHD();
        }
    }
}
