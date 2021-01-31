package org.pentaho.di.trans.steps.redisdelete;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 *  The Redis Delete step delete value objects, by the given key names, from Redis server(s).
 * @author minjie.qu
 * @createDate 2019年1月6日 下午4:35:18
 * @version v0.1
 * @describe  从Redis删除Key，定义kettle插件redis find del的UI Swing的对话框
 */
public class RedisDeleteDialog extends BaseStepDialog implements StepDialogInterface {
    private static Class<?> PKG = RedisDeleteMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

    private RedisDeleteMeta delete;
    private boolean gotPreviousFields = false;
    private RowMetaInterface previousFields;

    private Label wlKeyField;
    private CCombo wKeyField;
    private FormData fdlKeyField, fdKeyField;

    private Label wlValueField;
    private TextVar wValueField;
    private FormData fdlValueField, fdValueField;
    
    private Label wlTypeField;
    private CCombo wTypeField;
    private FormData fdlTypeField, fdTypeField;

    private Label wlMasterName;
    private TextVar wMasterName;
    private FormData fdlMasterName, fdMasterName;

    private Composite wServersComp;
    private Label wlServers;
    private TableView wServers;
    private FormData fdlServers, fdServers;

    public RedisDeleteDialog(Shell parent, Object in, TransMeta tr, String sname) {
        super(parent, (BaseStepMeta) in, tr, sname);
        delete = (RedisDeleteMeta) in;
    }

    public String open() {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, delete);

        ModifyListener lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                delete.setChanged();
            }
        };
        changed = delete.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.Shell.Title"));

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Stepname line
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.Stepname.Label"));
        props.setLook(wlStepname);
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment(0, 0);
        fdlStepname.right = new FormAttachment(middle, -margin);
        fdlStepname.top = new FormAttachment(0, margin);
        wlStepname.setLayoutData(fdlStepname);
        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        wStepname.addModifyListener(lsMod);
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment(middle, 0);
        fdStepname.top = new FormAttachment(0, margin);
        fdStepname.right = new FormAttachment(100, 0);
        wStepname.setLayoutData(fdStepname);

        // Key field
        wlKeyField = new Label(shell, SWT.RIGHT);
        wlKeyField.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.KeyField.Label"));
        props.setLook(wlKeyField);
        fdlKeyField = new FormData();
        fdlKeyField.left = new FormAttachment(0, 0);
        fdlKeyField.right = new FormAttachment(middle, -margin);
        fdlKeyField.top = new FormAttachment(wStepname, margin);
        wlKeyField.setLayoutData(fdlKeyField);
        wKeyField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
        props.setLook(wKeyField);
        wKeyField.addModifyListener(lsMod);
        fdKeyField = new FormData();
        fdKeyField.left = new FormAttachment(middle, 0);
        fdKeyField.top = new FormAttachment(wStepname, margin);
        fdKeyField.right = new FormAttachment(100, 0);
        wKeyField.setLayoutData(fdKeyField);
        wKeyField.addFocusListener(new FocusListener() {
            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
            }

            public void focusGained(org.eclipse.swt.events.FocusEvent e) {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                getFieldsInto(wKeyField);
                shell.setCursor(null);
                busy.dispose();
            }
        });
        
        // Value field
        wlValueField = new Label(shell, SWT.RIGHT);
        wlValueField.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.ValueField.Label"));
        props.setLook(wlValueField);
        fdlValueField = new FormData();
        fdlValueField.left = new FormAttachment(0, 0);
        fdlValueField.right = new FormAttachment(middle, -margin);
        fdlValueField.top = new FormAttachment(wKeyField, margin);
        wlValueField.setLayoutData(fdlValueField);
        wValueField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wValueField);
        wValueField.addModifyListener(lsMod);
        fdValueField = new FormData();
        fdValueField.left = new FormAttachment(middle, 0);
        fdValueField.top = new FormAttachment(wKeyField, margin);
        fdValueField.right = new FormAttachment(100, 0);
        wValueField.setLayoutData(fdValueField);

        // Value Type field
        wlTypeField = new Label(shell, SWT.RIGHT);
        wlTypeField.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.TypeField.Label"));
        props.setLook(wlTypeField);
        fdlTypeField = new FormData();
        fdlTypeField.left = new FormAttachment(0, 0);
        fdlTypeField.right = new FormAttachment(middle, -margin);
        fdlTypeField.top = new FormAttachment(wValueField, margin);
        wlTypeField.setLayoutData(fdlTypeField);
        wTypeField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
        props.setLook(wTypeField);
        wTypeField.addModifyListener(lsMod);
        fdTypeField = new FormData();
        fdTypeField.left = new FormAttachment(middle, 0);
        fdTypeField.top = new FormAttachment(wValueField, margin);
        fdTypeField.right = new FormAttachment(100, 0);
        wTypeField.setLayoutData(fdTypeField);
        wTypeField.setItems(ValueMeta.getAllTypes());
        wTypeField.select(1);
        
        // Master Name
        wlMasterName = new Label(shell, SWT.RIGHT);
        wlMasterName.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.MasterName.Label"));
        props.setLook(wlMasterName);
        fdlMasterName = new FormData();
        fdlMasterName.left = new FormAttachment(0, 0);
        fdlMasterName.right = new FormAttachment(middle, -margin);
        fdlMasterName.top = new FormAttachment(wTypeField, margin);
        wlMasterName.setLayoutData(fdlMasterName);
        wMasterName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wMasterName);
        wMasterName.addModifyListener(lsMod);
        fdMasterName = new FormData();
        fdMasterName.left = new FormAttachment(middle, 0);
        fdMasterName.top = new FormAttachment(wTypeField, margin);
        fdMasterName.right = new FormAttachment(100, 0);
        wMasterName.setLayoutData(fdMasterName);

        ColumnInfo[] colinf =
                new ColumnInfo[]{
                        new ColumnInfo(BaseMessages.getString(PKG, "RedisDeleteDialog.HostName.Column"),
                                ColumnInfo.COLUMN_TYPE_TEXT, false),
                        new ColumnInfo(BaseMessages.getString(PKG, "RedisDeleteDialog.Port.Column"),
                                ColumnInfo.COLUMN_TYPE_TEXT, false),
                        new ColumnInfo(BaseMessages.getString(PKG, "RedisDeleteDialog.Auth.Column"),
                                ColumnInfo.COLUMN_TYPE_TEXT, false),};

        // Servers
        wServersComp = new Composite(wMasterName, SWT.NONE);
        props.setLook(wServersComp);

        FormLayout fileLayout = new FormLayout();
        fileLayout.marginWidth = 3;
        fileLayout.marginHeight = 3;
        wServersComp.setLayout(fileLayout);

        wlServers = new Label(shell, SWT.RIGHT);
        wlServers.setText(BaseMessages.getString(PKG, "RedisDeleteDialog.Servers.Label"));
        props.setLook(wlServers);
        fdlServers = new FormData();
        fdlServers.left = new FormAttachment(0, 0);
        fdlServers.right = new FormAttachment(middle / 4, -margin);
        fdlServers.top = new FormAttachment(wMasterName, margin);
        wlServers.setLayoutData(fdlServers);

        wServers =
                new TableView(transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | 3, colinf, 4/* FieldsRows */, lsMod,
                        props);

        fdServers = new FormData();
        fdServers.left = new FormAttachment(middle / 4, 0);
        fdServers.top = new FormAttachment(wMasterName, margin * 2);
        fdServers.right = new FormAttachment(100, 0);
        // fdServers.bottom = new FormAttachment( 100, 0 );
        wServers.setLayoutData(fdServers);

        // Some buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        setButtonPositions(new Button[]{wOK, wCancel}, margin, wServers);

        // Add listeners
        lsCancel = new Listener() {
            public void handleEvent(Event e) {
                cancel();
            }
        };
        lsOK = new Listener() {
            public void handleEvent(Event e) {
                ok();
            }
        };

        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);

        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        };

        wStepname.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        // Set the shell size, based upon previous time...
        setSize();

        getData();
        delete.setChanged(changed);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return stepname;
    }

    /**
     * Copy information from the meta-data input to the dialog fields.
     */
    public void getData() {
        if (!Const.isEmpty(delete.getKeyFieldName())) {
            wKeyField.setText(Const.NVL(delete.getKeyFieldName(),""));
        }
        if (!Const.isEmpty(delete.getValueFieldName())) {
            wValueField.setText(Const.NVL(delete.getValueFieldName(),""));
        }
        if (!Const.isEmpty(delete.getValueTypeName())) {
            wTypeField.setText(Const.NVL(delete.getValueTypeName(),""));
        }
        if (!Const.isEmpty(delete.getMasterName())) {
        	wMasterName.setText(Const.NVL(delete.getMasterName(),""));
        }

        int i = 0;
        Set<Map<String,String>> servers = delete.getServers();
        if (servers != null) {
        	Iterator<Map<String, String>> iterator = servers.iterator();
            while (iterator.hasNext()) {
            	Map<String,String> addr=iterator.next();
                TableItem item = wServers.table.getItem(i);
                int col = 1;

                item.setText(col++, Const.NVL(addr.get("hostname"),""));
                item.setText(col++, Const.NVL(addr.get("port"),""));
                item.setText(col++, Const.NVL(addr.get("auth"),""));
                i++;
            }
        }

        wServers.setRowNums();
        wServers.optWidth(true);

        wStepname.selectAll();
        wStepname.setFocus();
    }

    private void cancel() {
        stepname = null;
        delete.setChanged(changed);
        dispose();
    }

    private void ok() {
        if (Const.isEmpty(wStepname.getText()))
            return;

        stepname = wStepname.getText(); // return value
        delete.setKeyFieldName(wKeyField.getText());
        delete.setValueFieldName(wValueField.getText());
        delete.setValueTypeName(wTypeField.getText());
        delete.setMasterName(wMasterName.getText());

        int nrServers = wServers.nrNonEmpty();

        delete.allocate(nrServers);

        Set<Map<String,String>> servers = delete.getServers();

        for (int i = 0; i < nrServers; i++) {
            TableItem item = wServers.getNonEmpty(i);
            Map<String,String> wServersmap=new HashMap<String, String>();
            wServersmap.put("hostname", item.getText(1));
            wServersmap.put("port", item.getText(2));
            wServersmap.put("auth", item.getText(3));
            servers.add(wServersmap);
        }
        delete.setServers(servers);

        dispose();
    }

    private void getFieldsInto(CCombo fieldCombo) {
        try {
            if (!gotPreviousFields) {
                previousFields = transMeta.getPrevStepFields(stepname);
            }

            String field = fieldCombo.getText();

            if (previousFields != null) {
                fieldCombo.setItems(previousFields.getFieldNames());
            }

            if (field != null)
                fieldCombo.setText(field);
            gotPreviousFields = true;

        } catch (KettleException ke) {
            new ErrorDialog(shell, BaseMessages.getString(PKG, "RedisDeleteDialog.FailedToGetFields.DialogTitle"),
                    BaseMessages.getString(PKG, "RedisDeleteDialog.FailedToGetFields.DialogMessage"), ke);
        }
    }
}
