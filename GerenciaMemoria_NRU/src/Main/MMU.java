package Main;

import Memoria.MemHD;
import Memoria.MemRam;
import Memoria.MemVirtual;
import Memoria.Pagina;

//esse novo
public class MMU {

    private MemRam MemoriaFisica;
    private MemVirtual MemoriaVirtual;
    private MemHD HD;
    private boolean posicaoReferenciada;
    private boolean posicaoModificada;
    private int IndicePagina;

    public MMU(MemVirtual MV_, MemRam Menram_, MemHD HD_) {
        this.MemoriaFisica = Menram_;
        this.MemoriaVirtual = MV_;
        this.HD = HD_;

    }

    public void receberComando(String s, int id) {
        //System.out.println("Processo: " + id);

        String[] t = s.split("-");

        if (t[1].contains("R")) {
            leitura(Integer.parseInt(t[0]));
        } else {
            escrita(Integer.parseInt(t[0]), Integer.parseInt(t[2]));
        }

    }

    private synchronized void escrita(int indiceVirtual, int escrita) {

        Integer posicaoNaRam = null;
        System.out.println("escrevendo:" + escrita + " na posicao: " + indiceVirtual);

       
        if (MemoriaVirtual.getPagina(indiceVirtual).existe()) {	//CASO A PAGINA JA EXISTA, FAZENDO UMA ATUALIZACAO	//se a pagina existe
            System.out.println(" A PAGINA JA EXISTE, FAZENDO UMA ATUALIZACAO");

            if (MemoriaVirtual.getPagina(indiceVirtual).isPresente()) {		//se a pagina ja esta na RAM
                System.out.println("a variavel ja esta na RAM");
                posicaoNaRam = MemoriaVirtual.getPagina(indiceVirtual).getMolduraPagina();//pega a posicao do endereço da ram
                MemoriaVirtual.getPagina(indiceVirtual).setModificada(true); //seta modificada para 1
                //MemoriaVirtual.getPagina(indiceVirtual).setReferenciada(true);
                //DUVIDA AQUI,
                MemoriaFisica.setValor(posicaoNaRam, escrita);//atualiza o valor que está na Ram

            } else { //CASO O VALOR NAO ESTEJA NA MEMORIA RAM

                System.out.println("O VALOR NAO ESTA NA MEMORIA RAM");
                this.liberarEspacoRam();
                this.HDparaRAM(indiceVirtual);
                int posicao_ram = MemoriaVirtual.getPagina(indiceVirtual).getMolduraPagina();
                MemoriaFisica.setValor(posicao_ram, escrita);			//escrevendo valor novo

            }

        } else { //CASO A PAGINA AINDA NAO EXISTA, FAZENDO UMA NOVA INSERCAO
            System.out.println(" PAGINA AINDA NAO EXISTA, FAZENDO UMA NOVA INSERCAO");
            posicaoNaRam = MemoriaFisica.getIndiceLivre();

            if (posicaoNaRam != null) { 		// EXISTE ESPACO LIVRE NA RAM!!!
                MemoriaVirtual.getPagina(indiceVirtual).setMolduraPagina(posicaoNaRam);
                MemoriaVirtual.getPagina(indiceVirtual).setPresente(true);
                MemoriaFisica.setValor(posicaoNaRam, escrita);

            } else { //CASO NAO EXISTA MEMORIA LIVRE PARA FAZER UMA INSERCAO
                System.out.println("NAO EXISTE MEMORIA LIVRE PARA FAZER UMA INSERCAO");
                this.liberarEspacoRam();
                posicaoNaRam = MemoriaFisica.getIndiceLivre();
                MemoriaVirtual.getPagina(indiceVirtual).setMolduraPagina(posicaoNaRam);
                MemoriaVirtual.getPagina(indiceVirtual).setPresente(true);
                MemoriaFisica.setValor(posicaoNaRam, escrita);

            }

        }

        System.out.println("ESCRITA FEITA COM SUCESSO");

    }

    private void leitura(int indiceVirtual) {
        System.out.println("Leitura em :" + indiceVirtual);

        Pagina leitura = this.MemoriaVirtual.getPagina(indiceVirtual);

        if (leitura.getMolduraPagina() == null) {
            //tentando ler pagina NULA
            System.out.println("LEITURA SENDO REALIZADA EM UMA PAGINA QUE NAO EXISTE ");
            return;
        }
        if (leitura != null) {
            if (leitura.isPresente()) {	//ela esta na ram
                System.out.println("Indece:" + indiceVirtual + " valor: " + this.MemoriaFisica.getValor(leitura.getMolduraPagina()));
                leitura.setReferenciada(true);
                System.out.println("oi, passando por aqui");
            } else { 	//caso esteja ausente (esta no HD).
                this.liberarEspacoRam();
                this.HDparaRAM(indiceVirtual);
                this.leitura(indiceVirtual);

            }
        }

    }

    private synchronized void liberarEspacoRam() {

        Integer IndexPage, IndiceLivreHD, IndiceRam, valorRam;
        IndexPage = algoritmoNRU();				//escolhendo quem vai sair da Ram para o HD

        System.out.println("O algoritmo NRU escolheu a pagina:" + IndexPage);
        IndiceLivreHD = HD.getIndiceLivreHD();			//pegando indice livre no HD

        IndiceRam = MemoriaVirtual.getPagina(IndexPage).getMolduraPagina();		//pegando a moldura da pagina
        MemoriaVirtual.getPagina(IndexPage).setMolduraPagina(IndiceLivreHD);		//apontando para o HD
        MemoriaVirtual.getPagina(IndexPage).setPresente(false);	//setando false, pois valor foi para o HD)
        valorRam = MemoriaFisica.getValor(IndiceRam);		//pegando valor na ram
        MemoriaFisica.setValor(IndiceRam, null);		//setando espaco da ram para null
        HD.setValorHD(IndiceLivreHD, valorRam);                  //inserindo no HD

        System.out.println("A pagina " + IndicePagina + " que tinha moduldura " + IndiceRam + " foi para o HD na posicao y" + IndiceLivreHD);
    }

    private synchronized void HDparaRAM(int indiceVirtual) {
        Integer molduraHD, valorHD, indiceLivreRAM;

        molduraHD = MemoriaVirtual.getPagina(indiceVirtual).getMolduraPagina();	//pegando valor da moldura que esta apontando para o HD
        valorHD = HD.getValorHD(molduraHD);					//pegando valor que esta  no HD
        HD.setValorHD(molduraHD, null);						//setando espaco do hd para null

        indiceLivreRAM = MemoriaFisica.getIndiceLivre();	//verificando indice livre na Ram
        MemoriaFisica.setValor(indiceLivreRAM, valorHD);        //COLOCANDO DE VOLTA NA RAM
        MemoriaVirtual.getPagina(indiceVirtual).setMolduraPagina(indiceLivreRAM);
        MemoriaVirtual.getPagina(indiceVirtual).setPresente(true);

        System.out.print("A pagina " + indiceVirtual + "\n"
                + " que tinha a moldura: y" + molduraHD + "\n"
                + " agora foi para RAM em 0x" + indiceLivreRAM);
    }

    public synchronized Integer algoritmoNRU() {

        System.out.println("Escolhendo a posição que irá remover");

        for (int i = 0; i <= 15; i++) {

            if (MemoriaVirtual.getPagina(i).isPresente()) {//verifica se o bit presente é true
                
                posicaoReferenciada = MemoriaVirtual.getPagina(i).isReferenciada();//verifica se a mo
                posicaoModificada = MemoriaVirtual.getPagina(i).isModificada();
                
                for(int j=0; j<=15; j++){

                if ((posicaoReferenciada == false) && (posicaoModificada == false)) {//classe 0

                   return IndicePagina = MemoriaVirtual.getPagina(i).getMolduraPagina();
                }
                
                }
                for(int m=0; m<=15; m++){

                if ((posicaoReferenciada == false) && (posicaoModificada == true)) {//classe 1

                   return IndicePagina = MemoriaVirtual.getPagina(i).getMolduraPagina();
                }
                
                }
                for(int n=0; n<=15; n++){

                if ((posicaoReferenciada == true) && (posicaoModificada == false)) {//classe 2

                   IndicePagina = MemoriaVirtual.getPagina(i).getMolduraPagina();
                    int count = 0;
                    count++;
                    if (count == 2) {
                        MemoriaVirtual.getPagina(i).setReferenciada(false);
                    }
                    return IndicePagina;
                }
                }
                for(int r=0; r<=15; r++){

                if ((posicaoReferenciada == true) && (posicaoModificada == true)) {//classe 3

                    IndicePagina = MemoriaVirtual.getPagina(i).getMolduraPagina();
                    int count = 0;
                    count++;
                    if (count == 2) {
                        MemoriaVirtual.getPagina(i).setReferenciada(false);
                    }
                    return IndicePagina;
                }
                }

            } else {

                MemoriaVirtual.getPagina(i).setReferenciada(false);
            }
        }

        return null;
    }

}
