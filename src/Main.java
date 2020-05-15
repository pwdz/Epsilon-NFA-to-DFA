public class Main {
    public static void main(String[] args) {
        NFAtoDFA nfAtoDFA = new NFAtoDFA("./NFA4.txt","./result.txt");
        nfAtoDFA.parseInput();
        nfAtoDFA.convertEpsilonNFAtoNFA();
        nfAtoDFA.convertToDFA();
        nfAtoDFA.printResultInFile();
    }
}
