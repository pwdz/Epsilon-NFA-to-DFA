public class Main {
    public static void main(String[] args) {
        NFAtoDFA nfAtoDFA = new NFAtoDFA("./NFA_Input_2.txt","./DFA_Output_2.txt");
        nfAtoDFA.parseInput();
        nfAtoDFA.convertEpsilonNFAtoNFA();
        nfAtoDFA.convertToDFA();
        nfAtoDFA.printResultInFile();
    }
}
