package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private static final String EXEC = "exec";
    private static final String OS_SYSTEM = "os.system";
    private static final String SUB_PROCESS = "subprocess";


    public CodeResult checkJavaMaliciousCode(String code){
        if(code.contains(EXEC)){
            return new CodeResult("It's impossible to execute this malicious code. It smells so BAAAAD!!!", STATUS.UNCOMPILED,0);
        }
        return null;
    }

    public CodeResult checkPythonMaliciousCode(String code){
        if(code.contains(OS_SYSTEM) || code.contains(SUB_PROCESS)){
            return new CodeResult("It's impossible to execute this malicious code. It smells so BAAAAD!!!", STATUS.UNCOMPILED,0);
        }
        return null;
    }
}
