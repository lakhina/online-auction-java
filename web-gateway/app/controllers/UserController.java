package controllers;

import com.example.auction.user.api.UserRegistration;
import com.example.auction.user.api.UserService;
import play.Configuration;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserController extends AbstractController {

    private final UserService userService;
    private final FormFactory formFactory;

    private final Boolean showInlineInstruction;

    @Inject
    public UserController(Configuration config, MessagesApi messagesApi, UserService userService, FormFactory formFactory) {
        super(messagesApi, userService);
        this.userService = userService;
        this.formFactory = formFactory;

        showInlineInstruction = config.getBoolean("online-auction.instruction.show");
    }

    public CompletionStage<Result> createUserForm() {
        return withUser(ctx(), userId ->
                loadNav(userId).thenApply(nav ->
                        ok(views.html.createUser.render(showInlineInstruction, formFactory.form(CreateUserForm.class), nav))
                )
        );
    }

    public CompletionStage<Result> createUser() {
        Http.Context ctx = ctx();
        return withUser(ctx, userId ->
                loadNav(userId).thenCompose(nav -> {
                    Form<CreateUserForm> form = formFactory.form(CreateUserForm.class).bindFromRequest(ctx.request());
                    if (form.hasErrors()) {
                        return CompletableFuture.completedFuture(ok(views.html.createUser.render(showInlineInstruction, form, nav)));
                    }

                    return userService.createUser()
                            .invoke(new UserRegistration(form.get().getName(), form.get().getEmail(), form.get().getPassword()))
                            .thenApply(user -> {
                                ctx.session().put("user", user.getId().toString());
                                return redirect(ProfileController.defaultProfilePage());
                            });
                })
        );
    }

    public Result currentUser(String userId) {
        session("user", userId);
        return ok("User switched");
    }
}
