package pt.andred.sirs1617.main.doctor;

import java.io.IOException;

import pt.andred.sirs1617.ui.Command;
import pt.andred.sirs1617.ui.Dialog;
import pt.andred.sirs1617.ui.DialogException;
import pt.andred.sirs1617.ws.cli.NotFenixClient;

public class SetPatient extends Command<NotFenixClient> {

	private String PRIVATE_TAG ="private_details";
	private String PUBLIC_TAG = "public_details";
	private String P_DETAILS_TAG = "P_DETAILS";;
	private String P_PUBLIC_DETAILS = "P_PUBLIC_DETAILS";


	public SetPatient(NotFenixClient client){
		super("Modify Patient Info", client);
	}

	@Override
	public void execute() throws DialogException, IOException {
		int k = 0;
		int option=0;
		String info="";
		String pname = "";
		while(k==0){
				k=1;
			pname = Dialog.IO().readString("Patient (0 to exit): ");
			try{
				int p = Integer.parseInt(pname);
				if(p==0){
					pt.andred.sirs1617.main.doctor.MenuBuilder.menuFor(_receiver);
					return;
				}
			} catch(Exception e){}
			Dialog.IO().println("What type info would you like to change?");
			Dialog.IO().println("private_details / public_details");
			String infoMode = Dialog.IO().readString();
			if(infoMode.matches(PRIVATE_TAG)){
				info = _receiver.getInfoPatient(pname, P_DETAILS_TAG);
				if(info==null){
						Dialog.IO().println("Patient inexistant or other server error");
						k=0;
						continue;
				}
				Dialog.IO().println("Current private info");
				option=1;
			}
			else if(infoMode.matches(PUBLIC_TAG)){
				info = _receiver.getInfoPatient(pname, P_PUBLIC_DETAILS);
				if(info==null){
						Dialog.IO().println("Patient inexistant or other server error");
						k=0;
						continue;
				}
				Dialog.IO().println("Current public info");
				option=2;
			}
			else{
				Dialog.IO().println("Not an Option");
				k=0;
			}
		}
		Dialog.IO().println(info);
		String new_info = Dialog.IO().readString("NewInfo:");
		Boolean success = false;
		if(option==1)
			success	= _receiver.setInfoPatient(pname, P_DETAILS_TAG, new_info);
		else if(option==2)
			success	= _receiver.setInfoPatient(pname, P_PUBLIC_DETAILS, new_info);
		else{
				Dialog.IO().println("An error occured");
				pt.andred.sirs1617.main.doctor.MenuBuilder.menuFor(_receiver);
				return;
		}

		if(success){
				Dialog.IO().println("Information Set");
		}else{
			Dialog.IO().println("Patient inexistant or other server error");
		}
	}
}
