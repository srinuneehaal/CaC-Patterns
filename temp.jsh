import com.example.cacex.service.plan.JsonModelMapper;
import com.example.cacex.model.TransactionFile;
import com.example.cacex.model.SideFile;
import java.nio.file.Path;

JsonModelMapper mapper = new JsonModelMapper();
TransactionFile tf = mapper.read(Path.of("changedfiles/transactions/transaction1.json"), TransactionFile.class);
System.out.println("txn type=" + tf.getType());
System.out.println("txn sideDef=" + tf.getSideDefinition());
SideFile sf = mapper.read(Path.of("changedfiles/sides/side1.json"), SideFile.class);
System.out.println("side key=" + sf.getSide());
System.out.println("side def=" + sf.getSideDefinition());
