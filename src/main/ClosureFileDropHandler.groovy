package main

import java.awt.datatransfer.*
import java.security.NoSuchAlgorithmException
import javax.swing.TransferHandler
import javax.swing.TransferHandler.DropLocation
import javax.swing.TransferHandler.TransferSupport
import javax.swing.JComponent
import javax.swing.JTextPane

class ClosureFileDropHandler extends TransferHandler 
{
	Closure importFileClosure = {}
	
	ClosureFileDropHandler(Closure importFileClosure)
	{
		this.importFileClosure = importFileClosure
	}
     
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        super.exportToClipboard(comp, clip, action);
    }
     
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
     
    protected Transferable createTransferable(JComponent c){
        JTextPane aTextPane = (JTextPane)c;

		
		//Normally, copy only the selected part of the document
		def start = aTextPane.getSelectionStart()
		def end = aTextPane.getSelectionEnd()
		
		//If nothing is selected, copy the entire document
		if(end == start)
		{
			start = aTextPane.getDocument().getStartPosition().getOffset()
			end = aTextPane.getDocument().getEndPosition().getOffset()
		}
		
		//This gets the selection as html. 
		def edkit = aTextPane.getEditorKit()
		def doc = aTextPane.getDocument()
		def sw = new StringWriter()
		edkit.write(sw, doc, start, end-start)
		
		//Convert <p> and <br> to newlines, <li> to tab, and gets rid of all other html tags.
		String htmlString = sw.toString()
		htmlString = htmlString.replaceAll(/[\r\n]*/, "") //replace all \r and \n
		htmlString = htmlString.replaceAll(/  */, " ") //replace all spaces with a single space
		htmlString = htmlString.replaceAll(/  */, " ") //replace all spaces with a single space
		htmlString = htmlString.replaceAll(/<br>/, "\r\n") //replace all <br> with \r\n
		htmlString = htmlString.replaceAll(/<p>/, "\r\n") //replace all <br> with \r\n\r\n
		htmlString = htmlString.replaceAll(/<\/p>/, "\r\n") //replace all <br> with \r\n\r\n
		htmlString = htmlString.replaceAll(/<li>/, "\r\n\t* ") //replace all <li> with \r\n\t* 
		htmlString = htmlString.replaceAll(/<[^>]*>/, "")
		htmlString = htmlString.replaceAll(/&gt;/, ">")
		htmlString = htmlString.replaceAll(/&lt;/, "<")
		htmlString = htmlString.replaceAll(/&amp;/, "&")
		htmlString = htmlString.replaceAll(/\r\n */, "\r\n") //replace spaces at start of line with nothing
		htmlString = htmlString.replaceAll(/  *\r\n/, "\r\n") //replace spaces at end of line with nothing
		htmlString = htmlString.trim()
		System.out.println("html->text:\r\n" + htmlString);
		return new StringSelection(htmlString);
    }
	
	public boolean canImport(TransferSupport supp) 
	{
		/* return false if the drop doesn't contain a list of files */
		if(!supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) 
		{
			return false;
		}

		boolean copySupported = (COPY & supp.getSourceDropActions()) == COPY;

		if (copySupported) 
		{
			supp.setDropAction(COPY);
			return true;
		}

		return false;
	}

	public boolean importData(TransferSupport supp) 
	{
		if (!canImport(supp)) 
		{
			return false;
		}

		/* get the Transferable */
		Transferable t = supp.getTransferable();

		try 
		{

			Object data = t.getTransferData(DataFlavor.javaFileListFlavor);

			List fileList = (List)data;

			fileList.each{currentfile->
				importFileClosure.call(currentfile)
			}
		} 
		catch (UnsupportedFlavorException e) 
		{
			return false;
		} 
		catch (IOException e) 
		{
			return false;
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}