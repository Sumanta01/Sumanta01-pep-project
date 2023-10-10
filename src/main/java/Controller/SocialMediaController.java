package Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.ExceptionService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerAccount);
        app.post("/login", this::loginAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessageById);
        app.get("/accounts/{account_id}/messages",
                this::getMessagesByAccountId);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
       context.json("sample text");
    }

    private void registerAccount(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        try {
            Account registeredAccount = accountService.createAccount(account);

            
            context.json(mapper.writeValueAsString(registeredAccount));
        } catch (ExceptionService e) {
            
            context.status(400);
        }
    }

    
    private void loginAccount(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); 
                                                  
        Account account = mapper.readValue(context.body(), Account.class);

        try {
            Optional<Account> loggedInAccount = accountService
                    .validateLogin(account);
            if (loggedInAccount.isPresent()) {
                
                context.json(mapper.writeValueAsString(loggedInAccount));
                context.sessionAttribute("logged_in_account",
                        loggedInAccount.get());
                context.json(loggedInAccount.get());
            } else {
                
                context.status(401);
            }
        } catch (ExceptionService e) {
           
            context.status(401);
        }
    }

    
    private void createMessage(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(context.body(), Message.class);
        try {
            Optional<Account> account = accountService
                    .getAccountById(mappedMessage.getPosted_by());
            Message message = messageService.createMessage(mappedMessage,
                    account);
            context.json(message);
        } catch (ExceptionService e) {
           
            context.status(400);
        }
    }

    
    private void getAllMessages(Context context) {

        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    

    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                ctx.json(message.get());
            } else {
                
                ctx.status(200); 
                                 
                ctx.result(""); 
            }
        } catch (NumberFormatException e) {
            ctx.status(400); 
        } catch (ExceptionService e) {
            ctx.status(200); 
            ctx.result(""); 
        }
    }

    
    private void deleteMessageById(Context context) {
        try {
            
            int id = Integer.parseInt(context.pathParam("message_id"));

            
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                
                messageService.deleteMessage(message.get());
                context.status(200);
               
                context.json(message.get());
            } else {
               
                context.status(200);
            }
        } catch (ExceptionService e) {
            
            context.status(200);
        }
    }

    
    private void updateMessageById(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(context.body(), Message.class);
        try {
            int id = Integer.parseInt(context.pathParam("message_id"));
            mappedMessage.setMessage_id(id);

            
            Message messageUpdated = messageService
                    .updateMessage(mappedMessage);

            
            context.json(messageUpdated);

        } catch (ExceptionService e) {
            
            context.status(400);
        }
    }

   
    private void getMessagesByAccountId(Context context) {
        try {
            int accountId = Integer.parseInt(context.pathParam("account_id"));

            
            List<Message> messages = messageService
                    .getMessagesByAccountId(accountId);
            if (!messages.isEmpty()) {
                
                context.json(messages);
            } else {
                
                context.json(messages);
                context.status(200);
            }
        } catch (ExceptionService e) {
            
            context.status(400);
        }
    }

     

}