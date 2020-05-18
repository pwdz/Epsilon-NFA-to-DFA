public class Main {
    public static void main(String[] args) {
        NFAtoDFA nfAtoDFA = new NFAtoDFA("./Tests/Inputs/NFA_INPUT_2.txt");
        nfAtoDFA.parseInput();
        nfAtoDFA.convertEpsilonNFAtoNFA();
        nfAtoDFA.convertToDFA();
        nfAtoDFA.printResultInFile();
    }
}
