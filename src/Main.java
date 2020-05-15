public class Main {
    public static void main(String[] args) {
        NFAtoDFA nfAtoDFA = new NFAtoDFA();
        nfAtoDFA.parseInput();
        nfAtoDFA.convertEpsilonNFAtoNFA();
//        nfAtoDFA.printNFATrans();
        nfAtoDFA.convertToNFA();
        nfAtoDFA.printTrans();
    }
}
