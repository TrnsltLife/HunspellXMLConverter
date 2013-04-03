package main

import javax.swing.*
import java.awt.datatransfer.*

class JTextPaneHTMLTransferHandler extends TransferHandler
{
     
    DataFlavor htmlFlavor = DataFlavor.stringFlavor;
     
    public JTextPaneHTMLTransferHandler(){
        System.out.println("Constructor of TransferHandler");
        try{
            htmlFlavor = new DataFlavor("text/html;class=java.lang.String");
        }catch (Exception e){
            System.out.println("Flavor creation failed");
        }
    }
     
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        System.out.println("method exportToClipboard");
        super.exportToClipboard(comp, clip, action);
    }
     
    public int getSourceActions(JComponent c) {
        System.out.println("method getSourceActions");
        return COPY_OR_MOVE;
    }
     
    protected Transferable createTransferable(JComponent c){
        System.out.println("method createTransferable");
        JTextPane aTextPane = (JTextPane)c;
         
        String textString = aTextPane.getSelectedText();
         
        return new StringSelection(textString);
    }
     
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors){
        System.out.println("method canImport");
        System.out.println(htmlFlavor);
        System.out.println();
        boolean retour = false;
        for(int i=0; i<transferFlavors.length; i++){
            System.out.println(transferFlavors[i]);
            if(htmlFlavor.equals(transferFlavors[i])){
                retour = true;
            }
        }
         
        return retour;
    }
     
    public boolean importData(JComponent comp, Transferable t){
        System.out.println("method importData");
        if(canImport(comp, t.getTransferDataFlavors())){
            System.out.println("canImport is true");
            String transferString = "";
            try{
                transferString = (String)t.getTransferData(htmlFlavor);
            }catch (Exception e){
                System.out.println("transferString failed");
            }
            JTextPane srcTextPane = (JTextPane)comp;
            srcTextPane.setText(transferString);
            return true;
        }
        System.out.println("canImport is false");
        return false;
    }
}