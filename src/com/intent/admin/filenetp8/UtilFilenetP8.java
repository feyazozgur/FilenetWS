package com.intent.admin.filenetp8;

import com.filenet.wcm.api.BadReferenceException;
import com.filenet.wcm.api.BaseObject;
import com.filenet.wcm.api.BaseObjects;
import com.filenet.wcm.api.Choice;
import com.filenet.wcm.api.ChoiceList;
import com.filenet.wcm.api.ChoiceLists;
import com.filenet.wcm.api.ClassDescription;
import com.filenet.wcm.api.ClassDescriptions;
import com.filenet.wcm.api.CustomObject;
import com.filenet.wcm.api.Document;
import com.filenet.wcm.api.Domain;
import com.filenet.wcm.api.Domains;
import com.filenet.wcm.api.EntireNetwork;
import com.filenet.wcm.api.Folder;
import com.filenet.wcm.api.GettableObject;
import com.filenet.wcm.api.Link;
import com.filenet.wcm.api.Links;
import com.filenet.wcm.api.ObjectFactory;
import com.filenet.wcm.api.ObjectStore;
import com.filenet.wcm.api.ObjectStores;
import com.filenet.wcm.api.Permission;
import com.filenet.wcm.api.Permissions;
import com.filenet.wcm.api.Property;
import com.filenet.wcm.api.PropertyDescription;
import com.filenet.wcm.api.PropertyDescriptions;
import com.filenet.wcm.api.PropertyNotFoundException;
import com.filenet.wcm.api.ReadableMetadataObject;
import com.filenet.wcm.api.ReadableSecurityObject;
import com.filenet.wcm.api.Session;
import com.filenet.wcm.api.TransportInputStream;
import com.filenet.wcm.api.Value;
import com.filenet.wcm.api.Values;
import com.filenet.wcm.api.WriteableSecurityObject;
import com.intent.admin.IUtils;
import com.intent.admin.file.UtilFile;
import com.intent.admin.filenetp8.p8.Data;
import com.intent.admin.filenetp8.p8.P8Template;
import com.intent.admin.filenetp8.p8.SearchUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class UtilFilenetP8 implements IUtils {
	private boolean uniquenessConstraint = true;
	private String multivalueSplit = ";";
	private HashMap securitytypeUser = new HashMap();
	private HashMap securityLevelsMap = new HashMap();
	private HashMap securityType = new HashMap();
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	private static String timeZone = "GMT-5:00";
	private static String patternDate = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
	private static String class_String = "java.lang.String";
	private static String class_Integer = "java.lang.Integer";
	private static String class_Date = "java.util.Date";
	private static String class_Boolean = "java.lang.Boolean";
	private Logger log = Logger.getLogger(UtilFilenetP8.class);
	private String user = null;
	private String password = null;
	private String configFile = null;
	private Session session = null;
	private String store = null;
	private String urlWorkplace = null;

	public UtilFilenetP8() {
		this.securitytypeUser.put("USER", new Integer(2000));
		this.securitytypeUser.put("GROUP", new Integer(2001));

		this.securityLevelsMap.put("FULL_CONTROL", new Integer(999415));

		this.securityLevelsMap.put("READ", new Integer(131201));

		this.securityLevelsMap.put("VIEW", new Integer(131073));

		this.securityLevelsMap.put("WRITE_DEFAULT", new Integer(132499));

		this.securityLevelsMap.put("MAJOR_VERSION_DOCUMENT", new Integer(132567));
		this.securityLevelsMap.put("MINOR_VERSION_DOCUMENT", new Integer(132563));

		this.securityType.put("ALLOW", new Integer(1));
		this.securityType.put("DENY", new Integer(2));
	}

	public List getOsNames() {
		List names = new ArrayList();
		EntireNetwork network = ObjectFactory.getEntireNetwork(getSession());
		Domains doms = network.getDomains();
		Domain dom = (Domain) doms.get(0);
		ObjectStores objstores = dom.getObjectStores();

		ObjectStore objstore = null;
		String objstoreName = null;

		Iterator iterator = objstores.iterator();
		while (iterator.hasNext()) {
			try {
				objstore = (ObjectStore) iterator.next();

				objstoreName = objstore.getPropertyStringValue("DisplayName");
				names.add(objstoreName);
			} catch (PropertyNotFoundException e) {
				e.printStackTrace();
			}
		}
		return names;
	}

	public String getUrlWorkplace() {
		if (!this.urlWorkplace.endsWith("/")) {
			this.urlWorkplace = this.urlWorkplace.concat("/");
		}
		return this.urlWorkplace;
	}

	public void setUrlWorkplace(String urlWorkplace) {
		this.urlWorkplace = urlWorkplace;
	}

	public UtilFilenetP8(String user, String password, String store, String server) {
		this.securitytypeUser.put("USER", new Integer(2000));
		this.securitytypeUser.put("GROUP", new Integer(2001));

		this.securityLevelsMap.put("FULL_CONTROL", new Integer(999415));

		this.securityLevelsMap.put("READ", new Integer(131201));

		this.securityLevelsMap.put("VIEW", new Integer(131073));

		this.securityLevelsMap.put("WRITE_DEFAULT", new Integer(132499));

		this.securityLevelsMap.put("MAJOR_VERSION_DOCUMENT", new Integer(132567));
		this.securityLevelsMap.put("MINOR_VERSION_DOCUMENT", new Integer(132563));

		this.securityType.put("ALLOW", new Integer(1));
		this.securityType.put("DENY", new Integer(2));

		UtilFile file = new UtilFile();
		java.util.Properties pr = System.getProperties();
		UtilFile.write(pr.getProperty("user.home") + "/file.properties",
				P8Template.contentWcmApi.toString().replaceAll("localhost", server), false);

		Conect(user, password, pr.getProperty("user.home") + "/file.properties");
		this.store = store;
	}

	public UtilFilenetP8(String user, String password, String store, InputStream inputStream) {
		this.securitytypeUser.put("USER", new Integer(2000));
		this.securitytypeUser.put("GROUP", new Integer(2001));

		this.securityLevelsMap.put("FULL_CONTROL", new Integer(999415));

		this.securityLevelsMap.put("READ", new Integer(131201));

		this.securityLevelsMap.put("VIEW", new Integer(131073));

		this.securityLevelsMap.put("WRITE_DEFAULT", new Integer(132499));

		this.securityLevelsMap.put("MAJOR_VERSION_DOCUMENT", new Integer(132567));
		this.securityLevelsMap.put("MINOR_VERSION_DOCUMENT", new Integer(132563));

		this.securityType.put("ALLOW", new Integer(1));
		this.securityType.put("DENY", new Integer(2));

		UtilFile file = new UtilFile();
		java.util.Properties pr = System.getProperties();

		Conect(user, password, inputStream);
		this.store = store;
	}

	public UtilFilenetP8(String user, String password, String store) {
		this.securitytypeUser.put("USER", new Integer(2000));
		this.securitytypeUser.put("GROUP", new Integer(2001));

		this.securityLevelsMap.put("FULL_CONTROL", new Integer(999415));

		this.securityLevelsMap.put("READ", new Integer(131201));

		this.securityLevelsMap.put("VIEW", new Integer(131073));

		this.securityLevelsMap.put("WRITE_DEFAULT", new Integer(132499));

		this.securityLevelsMap.put("MAJOR_VERSION_DOCUMENT", new Integer(132567));
		this.securityLevelsMap.put("MINOR_VERSION_DOCUMENT", new Integer(132563));

		this.securityType.put("ALLOW", new Integer(1));
		this.securityType.put("DENY", new Integer(2));

		Conect(user, password);
		this.store = store;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStore() {
		return this.store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	private void Conect(String puser, String ppassword, String pathConfig) {
		this.user = puser;
		this.password = ppassword;
		this.configFile = pathConfig;
		this.session = ObjectFactory.getSession("UtilFilenetP8", Session.DEFAULT, this.user, this.password);
		if (pathConfig != null) {
			try {
				getSession().setConfiguration(new FileInputStream(pathConfig));
			} catch (FileNotFoundException e) {
				if (this.log.isDebugEnabled()) {
					this.log.debug(e.getMessage());
				}
			}
		}
		getSession().verify();
	}

	private void Conect(String puser, String ppassword, InputStream stream) {
		this.user = puser;
		this.password = ppassword;

		this.session = ObjectFactory.getSession("UtilFilenetP8", Session.DEFAULT, this.user, this.password);
		if (stream != null) {
			getSession().setConfiguration(stream);
		}
		getSession().verify();
	}

	public boolean isConnected() {
		if (this.session != null) {
			return true;
		}
		return false;
	}

	private void Conect(String puser, String ppassword) {
		this.user = puser;
		this.password = ppassword;
		this.session = ObjectFactory.getSession("UtilFilenetP8", Session.DEFAULT, this.user, this.password);
		getSession().verify();
	}

	private void Conect() {
		if (getSession() == null) {
			if (this.configFile != null) {
				Conect(this.user, this.password, this.configFile);
			} else {
				Conect(this.user, this.password);
			}
		}
	}

	private void Conect(boolean createNew) {
		if (this.configFile != null) {
			Conect(this.user, this.password, this.configFile);
		} else {
			Conect(this.user, this.password);
		}
	}

	public void disconnect() {
		this.session = null;
	}

	public List getContainnes(Object folder, int objectType) {
		List objects = new ArrayList();
		BaseObjects retrievedDocs = null;
		if ((folder instanceof Folder)) {
			retrievedDocs = ((Folder) folder).getContainees(new int[] { objectType });
		} else if ((folder instanceof String)) {
			retrievedDocs = ((Folder) getObject(2, (String) folder)).getContainees(new int[] { objectType });
		}
		Object[] objects2 = retrievedDocs.toArray();
		for (int i = 0; i < objects2.length; i++) {
			objects.add((BaseObject) objects2[i]);
		}
		return objects;
	}

	public List getDocuments(Object folder) {
		return getContainnes(folder, 1);
	}

	public List getFolders(Object folder) {
		return getContainnes(folder, 2);
	}

	public List getCustomObjects(Object folder) {
		return getContainnes(folder, 15);
	}

	public List getContainnes(Object folder) {
		List objects = new ArrayList();

		BaseObjects retrievedDocs = null;
		if ((folder instanceof Folder)) {
			retrievedDocs = ((Folder) folder).getContainees();
		} else if ((folder instanceof String)) {
			retrievedDocs = ((Folder) getObject(2, (String) folder)).getContainees();
		}
		Object[] objects2 = retrievedDocs.toArray();
		for (int i = 0; i < objects2.length; i++) {
			objects.add((BaseObject) objects2[i]);
		}
		System.out.println();
		return objects;
	}

	public String getObjectURL(BaseObject object, boolean download) {
		String typeObject = null;
		if ((object instanceof Document)) {
			typeObject = "Document";
		} else if ((object instanceof Folder)) {
			typeObject = "Folder";
		} else if ((object instanceof CustomObject)) {
			typeObject = "CustomObject";
		}
		if (!this.urlWorkplace.endsWith("/")) {
			this.urlWorkplace = this.urlWorkplace.concat("/");
		}
		String cad = null;
		if (download) {
			cad =

					this.urlWorkplace + "getContent?mode=download&objectType=" + typeObject + "&objectStoreName="
							+ this.store + "&id=" + object.getId();
		} else {
			cad =

					this.urlWorkplace + "properties/ObjectInfo.jsp?objectStoreName=" + this.store + "&objectType="
							+ typeObject + "&versionStatus=1&id=" + object.getId();
		}
		return cad;
	}

	public Document fileDocument(Object destination, Object document, String name) {
		Folder destinationFolder = null;
		Document documentSource = null;
		InputStream file = null;
		if ((destination instanceof String)) {
			destinationFolder = (Folder) getObject(2, destination.toString());
		} else if ((destination instanceof Folder)) {
			destinationFolder = (Folder) destination;
		} else {
			this.log.error("The destination must be a folder or the id");
		}
		if ((document instanceof String)) {
			try {
				file = new FileInputStream(document.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if ((document instanceof InputStream)) {
			file = (InputStream) document;
		}
		TransportInputStream in = new TransportInputStream(file);
		in.setFilename(name);

		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		documentSource = (Document) objectStore.createObject("{01A3A8C2-7AEC-11D1-A31B-0020AF9FBB1C}", null, null);
		documentSource.setContent(in, false, true);
		documentSource.file(destinationFolder, this.uniquenessConstraint);

		return documentSource;
	}

	public GettableObject getObject(int typeObject, String idOrPathObject) {
		Conect();
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		return objectStore.getObject(typeObject, idOrPathObject);
	}

	public HashMap getProperties(int typeObject, String idOrPathObject, boolean getAll) {
		Conect();
		return getProperties(getObject(typeObject, idOrPathObject), getAll);
	}

	public HashMap getProperties(BaseObject object, boolean getAll) {
		Conect();
		HashMap propsUtil = null;
		try {
			if (getAll) {
				com.filenet.wcm.api.Properties props = null;
				props = ((ReadableMetadataObject) object).getProperties();
				propsUtil = convertProperties(props);
			} else {
				ClassDescription cd = (ClassDescription) ((ReadableMetadataObject) object)
						.getPropertyValue("ClassDescription");
				PropertyDescriptions propDescs = cd.getPropertyDescriptions(false);
				propDescs = (PropertyDescriptions) propDescs.filterByProperty("IsSystemGenerated", 1, false);
				propDescs = (PropertyDescriptions) propDescs.filterByProperty("IsReadOnly", 1, false);
				propDescs = (PropertyDescriptions) propDescs.filterByProperty("Settability", 1, 0);
				List proList = new ArrayList();
				for (int i = 0; i < propDescs.size(); i++) {
					proList.add(((PropertyDescription) propDescs.get(i)).getPropertyStringValue("SymbolicName"));
				}
				com.filenet.wcm.api.Properties props = null;
				props = ((ReadableMetadataObject) object)
						.getProperties((String[]) proList.toArray(new String[proList.size()]));
				propsUtil = convertProperties(props);
			}
		} catch (PropertyNotFoundException e) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Error: " + e.getMessage());
			}
		}
		return propsUtil;
	}

	public Object getValueProperty(int typeObject, String idOrPathObject, String property) {
		Conect();
		HashMap p = getProperties(typeObject, idOrPathObject, true);
		return p.get(property);
	}

	private void setValuePropertyP8(Property p, Object o) throws Exception {
		PropertyDescription pdesc = (PropertyDescription) getObject(24, p.getName());

		Object b = null;
		if (this.session.isInBatch()) {
			b = this.session.suspendBatch();
		}
		int datatype = pdesc.getPropertyIntValue("DataType");
		if (b != null) {
			this.session.resumeBatch(b);
		}
		if (datatype == 8) {
			p.setValue((String) o);
		} else if (datatype == 6) {
			try {
				p.setValue(Integer.parseInt(o.toString()));
			} catch (Exception e) {
				throw new Exception("PARAMETER_EXCEPTION Invalid Int Value over property " + pdesc.getName() + " "
						+ e.getMessage());
			}
		} else if (datatype == 4) {
			try {
				Float.parseFloat(o.toString());
				p.setValue(o.toString());
			} catch (Exception e) {
				throw new Exception("PARAMETER_EXCEPTION Invalid Float Value over property " + pdesc.getName() + " "
						+ e.getMessage());
			}
		} else if (datatype == 3) {
			if (o.getClass().getName().equals(class_String)) {
				try {
					formatDate.setLenient(false);
					o = formatDate(o.toString());
					p.setValue(formatDate.parse(o.toString()));
				} catch (ParseException e) {
					throw new Exception("PARAMETER_EXCEPTION Invalid Date Value over property " + pdesc.getName() + " "
							+ e.getMessage());
				}
			} else if (o.getClass().getName().equals(class_Date)) {
				p.setValue((Date) o);
			}
		} else if (datatype == 2) {
			if ((o != null) && (("True".equalsIgnoreCase(o.toString())) || ("False".equalsIgnoreCase(o.toString())))) {
				p.setValue(Boolean.valueOf(o.toString()));
			} else {
				throw new Exception(
						"PARAMETER_EXCEPTION Invalid Boolean Value over property " + pdesc.getName() + " Value: " + o);
			}
		} else if (o.getClass().getName().equals("com.filenet.wcm.api.impl.ValuesImpl")) {
			p.setValue((Values) o);
		}
	}

	private void setValueValueP8(Value p, Object o) {
		if (o.getClass().getName().equals(class_String)) {
			p.setValue((String) o);
		} else if (o.getClass().getName().equals(class_Integer)) {
			p.setValue(Integer.parseInt(o.toString()));
		} else if (o.getClass().getName().equals(class_Date)) {
			p.setValue((Date) o);
		} else if (o.getClass().getName().equals(class_Boolean)) {
			p.setValue(Boolean.getBoolean(o.toString()));
		}
	}

	public boolean update(BaseObject object, String symbolicName, Object value) throws Exception {
		return setValuesProperties(object.getObjectType(), object.getId(), new String[] { symbolicName },
				new Object[] { value });
	}

	public boolean update(int objectType, String idOrPath, String symbolicName, Object value) throws Exception {
		BaseObject object = getObject(objectType, idOrPath);
		return setValuesProperties(object.getObjectType(), object.getId(), new String[] { symbolicName },
				new Object[] { value });
	}

	private com.filenet.wcm.api.Properties getP8Properties(String[] properties, Object[] values) throws Exception {
		com.filenet.wcm.api.Properties objProps = ObjectFactory.getProperties();
		for (int i = 0; i < properties.length; i++) {
			Property prop = ObjectFactory.getProperty(properties[i]);
			PropertyDescription pdesc = (PropertyDescription) getObject(24, prop.getName());

			int cardinality = pdesc.getPropertyIntValue("Cardinality");
			if (cardinality == 2) {
				Object[] obs = ((String) values[i]).split(this.multivalueSplit);
				Values vs = ObjectFactory.getValues();
				for (int j = 0; j < obs.length; j++) {
					Value v = ObjectFactory.getValue();
					setValueValueP8(v, obs[j]);
					vs.add(v);
				}
				prop.setValue(vs);
			} else if (values[i] != null) {
				setValuePropertyP8(prop, values[i]);
			}
			objProps.add(prop);
		}
		return objProps;
	}

	public boolean setValuesProperties(int typeObject, String idOrPathObject, String[] properties, Object[] values)
			throws Exception {
		GettableObject object = getObject(typeObject, idOrPathObject);
		boolean retorno = false;
		com.filenet.wcm.api.Properties objProps = getP8Properties(properties, values);
		try {
			((WriteableSecurityObject) object).setProperties(objProps);
			retorno = true;
		} catch (Exception e) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Error: " + e.getMessage());
			}
		}
		return retorno;
	}

	private static HashMap convertProperties(com.filenet.wcm.api.Properties ps) {
		HashMap props = new HashMap();
		Iterator it = ps.iterator();
		while (it.hasNext()) {
			Property p = (Property) it.next();
			try {
				if (p.getValue().getClass().getName().equals("com.filenet.wcm.api.impl.ValuesImpl")) {
					Values vls = p.getValuesValue();
					List l = new ArrayList();
					for (int i = 0; i < vls.size(); i++) {
						l.add(((Value) vls.get(i)).getValue());
					}
					if (l.size() > 0) {
						props.put(p.getName(), l);
					}
				} else if (p.getValue() != null) {
					props.put(p.getName(), p.getValue());
				}
			} catch (Exception e) {
				if (p.getValue() != null) {
					props.put(p.getName(), p.getValue());
				}
			}
		}
		return props;
	}

	public static String formatDate(String stringDate) {
		Pattern patron = Pattern.compile(patternDate);
		Matcher encaja = patron.matcher(stringDate);
		ArrayList al = new ArrayList();
		while (encaja.find()) {
			int inicio = encaja.start();
			int fin = encaja.end();
			String fecha = stringDate.substring(inicio, fin);
			al.add(fecha);
		}
		for (int i = 0; i < al.size(); i++) {
			String fecha = (String) al.get(i);
			try {
				TimeZone tx = TimeZone.getTimeZone(timeZone);
				formatDate.setTimeZone(tx);
				Date fec = formatDate.parse(fecha);
				tx = TimeZone.getTimeZone("GMT");
				formatDate.setTimeZone(tx);
				String fecact = formatDate.format(fec);
				stringDate = stringDate.replaceAll(fecha, fecact);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringDate;
	}

	public List query(String query) {
		Conect();
		List ret = null;
		if (getSession() != null) {
			System.out.println("Before fix format: " + query);
			query = formatDate(query);
			System.out.println("After fix format: " + query);
			List l = P8Template.getObjects(getSession(), this.store, query);
			if (l != null) {
				ret = new ArrayList();
				for (int i = 0; i < l.size(); i++) {
					List vars = (List) l.get(i);
					HashMap map = new HashMap();
					for (int j = 0; j < vars.size(); j++) {
						map.put(((Data) vars.get(j)).getName(), ((Data) vars.get(j)).getValue());
					}
					ret.add(map);
				}
			}
		}
		return ret;
	}

	public List query(String tableName, String[] columns, String[] filTterColumns, String[] filtterValues) {
		Conect();
		if (getSession() != null) {
			StringBuffer where = new StringBuffer(" ");
			if ((filTterColumns != null) && (filtterValues != null)
					&& (filTterColumns.length == filtterValues.length)) {
				where = new StringBuffer("WHERE (");
				for (int i = 0; i < filTterColumns.length; i++) {
					where.append(filTterColumns[i]).append(" ").append(filtterValues[i]).append(" AND ");
				}
				where = new StringBuffer(where.substring(0, where.length() - 4));
				where.append(")");
			}
			this.log.debug(where);
			List ret = null;
			List l = P8Template.getObjects(tableName, this.store, getSession(), "Id", columns, where.toString());
			if (l != null) {
				ret = new ArrayList();
				for (int i = 0; i < l.size(); i++) {
					List vars = (List) l.get(i);
					HashMap map = new HashMap();
					for (int j = 0; j < vars.size(); j++) {
						map.put(((Data) vars.get(j)).getName(), ((Data) vars.get(j)).getValue());
					}
					ret.add(map);
				}
			}
			return ret;
		}
		return null;
	}

	public int update(String tableName, String[] columnsFilter, String[] valuesFilter, String[] columnsToUpdate,
			Object[] valuesToUpdate) throws Exception {
		Conect();
		List l = query(tableName, new String[] { "Id" }, columnsFilter, valuesFilter);
		int updates = 0;
		int classType = getClassType(tableName);
		for (int i = 0; i < l.size(); i++) {
			HashMap map = (HashMap) l.get(i);
			String id = map.get("Id").toString();
			if (setValuesProperties(classType, id, columnsToUpdate, valuesToUpdate)) {
				updates++;
			}
		}
		return updates;
	}

	private int getClassType(String className) throws Exception {
		if ((className.equals("com.filenet.wcm.api.impl.DocumentImpl")) || (className.equalsIgnoreCase("Document"))) {
			return 1;
		}
		if ((className.equals("com.filenet.wcm.api.impl.FolderImpl")) || (className.equalsIgnoreCase("Document"))) {
			return 2;
		}
		if ((className.equals("com.filenet.wcm.api.impl.CustomObjImpl")) || (className.equalsIgnoreCase("Document"))) {
			return 15;
		}
		ClassDescription cls = (ClassDescription) getObject(25, className);
		int classType = 0;
		if (cls.isSubclassOf("Document")) {
			classType = 1;
		} else if (cls.isSubclassOf("Folder")) {
			classType = 2;
		} else if (cls.isSubclassOf("CustomObject")) {
			classType = 15;
		}
		return classType;
	}

	public int execute(String sql) {
		return 0;
	}

	public boolean delete(int classType, String idObject) {
		Conect();
		GettableObject object = getObject(classType, idObject);
		return delete(object);
	}

	public boolean delete(BaseObject object) {
		Conect();
		boolean retorno = false;
		switch (object.getObjectType()) {
		case 1:
			((Document) object).delete();
			retorno = true;
			break;
		case 15:
			((CustomObject) object).delete();
			retorno = true;
			break;
		case 2:
			if (((Folder) object).getContainees().size() > 0) {
				BaseObjects objects = ((Folder) object).getContainees();
				for (int i = 0; i < objects.size(); i++) {
					Object obj = objects.get(i);
					delete((BaseObject) obj);
				}
			}
			((Folder) object).delete();
			retorno = true;
		}
		return retorno;
	}

	public int delete(String tableName, String[] filTterColumns, String[] filtterValues) throws Exception {
		Conect();
		List l = query(tableName, new String[] { "Id" }, filTterColumns, filtterValues);
		int updates = 0;
		int classType = getClassType(tableName);
		for (int i = 0; i < l.size(); i++) {
			HashMap map = (HashMap) l.get(i);
			String id = map.get("Id").toString();
			if (delete(classType, id)) {
				updates++;
			}
		}
		return updates;
	}

	public Document createDocumentAndContent(String folderParent, String className, String[] columns, Object[] values,
			String urlContent) throws Exception {
		Conect();
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		com.filenet.wcm.api.Properties objProps = getP8Properties(columns, values);
		BaseObject object = null;
		object = objectStore.createObject(className, objProps, null);
		if ((urlContent != null) || (urlContent.trim().length() > 0)) {
			if (new File(urlContent).exists()) {
				TransportInputStream input = new TransportInputStream(new FileInputStream(urlContent));
				input.setFilename(urlContent);
				input.setMimeType(SearchUtils.getMimeType(urlContent));
				((Document) object).setContent(input, true, false);
			} else {
				this.log.debug("The document " + urlContent
						+ " doesn't exist or can't access. The Document is created without image");
			}
		}
		((Document) object).file((Folder) objectStore.getObject(2, folderParent), this.uniquenessConstraint);

		return (Document) object;
	}

	public Object insert(String tableName, String[] columns, Object[] values) throws Exception {
		Conect();
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		com.filenet.wcm.api.Properties objProps = getP8Properties(columns, values);
		BaseObject object = null;
		switch (getClassType(tableName)) {
		case 1:
			object = objectStore.createObject("{01A3A8C2-7AEC-11D1-A31B-0020AF9FBB1C}", objProps, null);
			((Document) object).file(objectStore.getRootFolder(), this.uniquenessConstraint);
			((Document) object).checkin(false);
			break;
		case 15:
			object = objectStore.createObject("{D32E4F58-AFB2-11D2-8BD6-00E0290F729A}", objProps, null);
			((CustomObject) object).file(objectStore.getRootFolder(), this.uniquenessConstraint);
			break;
		case 2:
			object = objectStore.getRootFolder().addSubFolder(getNameInProperties(objProps), objProps, null);
		}
		return object;
	}

	public Object insert(Object folderSource, String classType, String[] columns, Object[] values) throws Exception {
		Conect();
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		com.filenet.wcm.api.Properties objProps = getP8Properties(columns, values);
		Object object = null;
		Folder folder = null;
		if ((folderSource instanceof String)) {
			folder = (Folder) objectStore.getObject(2, (String) folderSource);
		} else if ((folderSource instanceof Folder)) {
			folder = (Folder) folderSource;
		}
		boolean existFolder = false;

		folder.getId();
		existFolder = true;
		if (existFolder) {
			switch (getClassType(classType)) {
			case 1:
				object = objectStore.createAndFileObject(classType, objProps, null, folder);

				break;
			case 15:
				object = objectStore.createAndFileObject(classType, objProps, null, folder);

				break;
			case 2:
				object = folder.addSubFolder(getNameInProperties(objProps), classType, objProps, null);
			}
		}
		return object;
	}

	public boolean setDocumentContent(Object pdoc, Object content) throws Exception {
		Conect();
		Document doc = null;
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		if ((pdoc instanceof String)) {
			doc = (Document) objectStore.getObject(1, (String) pdoc);
		} else if ((pdoc instanceof Document)) {
			doc = (Document) pdoc;
		} else {
			return false;
		}
		if ((content instanceof String)) {
			TransportInputStream input = new TransportInputStream(new FileInputStream((String) content));
			input.setFilename(content.toString());
			input.setMimeType(SearchUtils.getMimeType(content.toString()));
			doc.setContent(input, true, false);
		} else if ((content instanceof TransportInputStream)) {
			doc.setContent((TransportInputStream) content, true, false);
		} else if ((content instanceof FileInputStream)) {
			doc.setContent(new TransportInputStream((FileInputStream) content), true, false);
		} else if ((content instanceof List)) {
			for (int i = 0; i < ((List) content).size(); i++) {
				TransportInputStream tr = null;
				if ((((List) content).get(i) instanceof String)) {
					tr = new TransportInputStream(new FileInputStream((String) ((List) content).get(i)));
				} else if ((((List) content).get(i) instanceof TransportInputStream)) {
					tr = (TransportInputStream) ((List) content).get(i);
				} else if ((((List) content).get(i) instanceof FileInputStream)) {
					tr = new TransportInputStream((FileInputStream) ((List) content).get(i));
				}
				if (tr != null) {
					tr.setContentElement(i + 1);
					doc.setContent(tr, false, false);
				}
			}
			doc.checkin(false);
		}
		return true;
	}

	public String copyContentToSystemFolder(Object doc, String urlDest) {
		Conect();
		Document doc1 = null;
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());
		if ((doc instanceof Document)) {
			doc1 = (Document) doc;
		} else if ((doc instanceof String)) {
			doc1 = (Document) objectStore.getObject(1, (String) doc);
		}
		File dst;
		try {
			dst = new File(urlDest + File.separator + doc1.getFilename());
		} catch (Exception ex) {
			String name = null;
			try {
				TransportInputStream tr = doc1.getContent();
				name = tr.getFilename();
			} catch (Exception e) {
				this.log.debug(e);
			}
			dst = new File(urlDest + File.separator + name);
		}
		TransportInputStream src = doc1.getContent();
		try {
			OutputStream out = new FileOutputStream(dst);
			byte[] buf = new byte['?'];
			int len;
			while ((len = src.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			src.close();
			out.close();
		} catch (IOException e) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Error: " + e.getMessage());
			}
			return null;
		}
		OutputStream out;
		return dst.getAbsolutePath();
	}

	public TransportInputStream getContent(String idDocument) {
		Conect();
		ObjectStore objectStore = ObjectFactory.getObjectStore(this.store, getSession());

		Object o = ((Document) objectStore.getObject(1, idDocument)).getContent();
		return (TransportInputStream) o;
	}

	public boolean moveObjectIntoOS(Folder folder, Folder folder2, boolean includeSecurity, boolean deleteSource)
			throws Exception {
		ObjectStore storeSource = (ObjectStore) folder.getPropertyValue("ObjectStore");
		ObjectStore storeDestination = (ObjectStore) folder2.getPropertyValue("ObjectStore");
		moveObjectIntoOS(2, folder.getId(), storeSource, folder2.getId(), storeDestination, false, false);

		return true;
	}

	public boolean moveObjectIntoOS(int objectType, String idPathObject, String configFileSource, String storeSource,
			String idFolderDestination, String configFileDestination, String storeDestination, boolean includeSecurity,
			boolean deleteSource) {
		setConfigFile(configFileSource);
		setStore(storeSource);
		Conect(true);
		ObjectStore objectStoreSource = ObjectFactory.getObjectStore(this.store, getSession());

		setConfigFile(configFileDestination);
		setStore(storeDestination);
		Conect(true);
		ObjectStore objectStoreDest = ObjectFactory.getObjectStore(this.store, getSession());
		return moveObjectIntoOS(objectType, idPathObject, objectStoreSource, idFolderDestination, objectStoreDest,
				includeSecurity, deleteSource);
	}

	public boolean moveObjectIntoOS(int objectType, String idPathObject, ObjectStore storeSource,
			String idFolderDestination, ObjectStore storeDestination, boolean includeSecurity, boolean deleteSource) {
		GettableObject ob = storeSource.getObject(objectType, idPathObject);
		String classId = ob.getClassId();
		com.filenet.wcm.api.Properties propsOb = null;
		Permissions permsOb = null;
		try {
			propsOb = ((ReadableMetadataObject) ob).getProperties();
			if (includeSecurity) {
				permsOb = ((ReadableSecurityObject) ob).getPermissions();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (this.log.isDebugEnabled()) {
				this.log.debug("Source problem ", e);
			}
			return false;
		}
		com.filenet.wcm.api.Properties propsNew = ObjectFactory.getProperties();
		for (int i = 0; i < propsOb.size(); i++) {
			Property property = (Property) propsOb.get(i);
			PropertyDescription p = (PropertyDescription) storeSource.getObject(24, property.getName());
			try {
				if ((!p.getPropertyBooleanValue("IsSystemGenerated"))
						&& (!p.getPropertyBooleanValue("IsSystemOwned"))) {
					propsNew.add(property);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				if (this.log.isDebugEnabled()) {
					this.log.debug("Exception in property " + p.getName(), ex);
				}
			}
		}
		Folder f = (Folder) storeDestination.getObject(2, idFolderDestination);
		try {
			switch (objectType) {
			case 1:
				Document doc = (Document) storeDestination.createObject(classId, propsNew, permsOb);
				doc.file(f, this.uniquenessConstraint);
				try {
					doc.setContent(((Document) ob).getContent(), true, false);
				} catch (BadReferenceException ex) {
					ex.printStackTrace();
					if (this.log.isDebugEnabled()) {
						this.log.debug("Could`n get content in document " + ex.getMessage());
					}
				}
				if (deleteSource) {
					((Document) ob).delete();
				}
				break;
			case 2:
				Folder folderNew = f.addSubFolder(ob.getName(), classId, propsNew, permsOb);
				BaseObjects objects = ((Folder) ob).getContainees();
				for (int i = 0; i < objects.size(); i++) {
					moveObjectIntoOS(((BaseObject) objects.get(i)).getObjectType(),
							((BaseObject) objects.get(i)).getId(), storeSource, folderNew.getId(), storeDestination,
							includeSecurity, deleteSource);
				}
				if (deleteSource) {
					((Folder) ob).delete();
				}
				break;
			case 15:
				CustomObject custom = (CustomObject) storeDestination.createObject(classId, propsNew, permsOb);
				custom.file(f, this.uniquenessConstraint);
				if (deleteSource) {
					((CustomObject) ob).delete();
				}
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (this.log.isDebugEnabled()) {
				this.log.debug("Cant do operation " + classId + ex.getMessage());
			}
		}
		return true;
	}

	public List getPermissions(ReadableSecurityObject object) {
		List ls = new ArrayList();
		Permissions perm = object.getPermissions();
		Iterator itp = perm.iterator();
		while (itp.hasNext()) {
			HashMap map = new HashMap();
			Permission p = (Permission) itp.next();
			map.put("ObjectStore", getStore());
			map.put("ObjectType", new Integer(object.getObjectType()));
			map.put("Id", object.getId());
			map.put("Access", new Integer(p.getAccess()));
			map.put("AccessType", new Integer(p.getAccessType()));
			map.put("GranteeName", p.getGranteeName());
			ls.add(map);
		}
		return ls;
	}

	public boolean setPermissionsOnObject(int typeObject, String idObjtect, int level, int type, String user,
			int typeofUser) {
		Conect();
		Permissions newPerms = ObjectFactory.getPermissions();
		Permission newPerm = ObjectFactory.getPermission(level, type, user, typeofUser);
		GettableObject ob = getObject(typeObject, idObjtect);
		Permissions permsOb = ((ReadableSecurityObject) ob).getPermissions();
		Object[] objects = permsOb.toArray();
		for (int i = 0; i < objects.length; i++) {
			Object perm = objects[i];
			if (!((Permission) perm).getGranteeName().equalsIgnoreCase(user)) {
				newPerms.add(perm);
			}
		}
		newPerms.add(newPerm);
		((WriteableSecurityObject) ob).setPermissions(newPerms);
		return true;
	}

	public boolean setPermissionsOnObject(int typeObject, String idObject, String level, String type, String user,
			String typeofUser) throws Exception {
		if ((this.securityLevelsMap.containsKey(level)) && (this.securityType.containsKey(type))
				&& (this.securitytypeUser.containsKey(typeofUser))) {
			return setPermissionsOnObject(typeObject, idObject,
					((Integer) this.securityLevelsMap.get(level)).intValue(),
					((Integer) this.securityType.get(type)).intValue(), user,
					((Integer) this.securitytypeUser.get(typeofUser.toUpperCase())).intValue());
		}
		throw new Exception("Security Exception: Cant find Level/Type/principalType specified: " + level + "/" + type
				+ "/" + typeofUser);
	}

	public boolean moveObject(int typeObject, String idOrPathObject, String idFolderDestination,
			boolean includeSecurity, boolean deleteSource) {
		return moveObjectIntoOS(typeObject, idOrPathObject, ObjectFactory.getObjectStore(this.store, getSession()),
				idFolderDestination, ObjectFactory.getObjectStore(this.store, getSession()), includeSecurity,
				deleteSource);
	}

	public String getNameInProperties(com.filenet.wcm.api.Properties props) {
		String name = "";
		HashMap map = convertProperties(props);
		if (map.get("Name") != null) {
			name = (String) map.get("Name");
		} else if (map.get("DocumentTitle") != null) {
			name = (String) map.get("DocumentTitle");
		} else if (map.get("FolderName") != null) {
			name = (String) map.get("FolderName");
		}
		return name;
	}

	private List getValuesOfChoiceList(ChoiceList choiceList) {
		List objects = new LinkedList();
		for (int j = 0; j < choiceList.size(); j++) {
			Choice c2 = (Choice) choiceList.get(j);
			if (c2.hasContainedChoices()) {
				ChoiceList c3 = c2.getContainedChoices();
				if (c3 != null) {
					HashMap mapint = new HashMap();
					mapint.put(c3.getName(), getValuesOfChoiceList(c3));
					objects.add(mapint);
				}
			} else {
				objects.add(c2.getValue().toString());
			}
		}
		return objects;
	}

	public HashMap getValuesOfChoiceList(String idChoiceList) {
		Conect();
		HashMap map = new HashMap();
		Vector v = new Vector();
		v.add(idChoiceList);
		ChoiceLists ch = ObjectFactory.getObjectStore(this.store, getSession()).getChoiceLists(v, 0);
		if (ch != null) {
			for (int i = 0; i < ch.size(); i++) {
				ChoiceList c1 = (ChoiceList) ch.get(i);
				List objects = new ArrayList();
				for (int j = 0; j < c1.size(); j++) {
					Choice c2 = (Choice) c1.get(j);
					if (c2.hasContainedChoices()) {
						ChoiceList c3 = c2.getContainedChoices();
						HashMap mapint = new HashMap();
						if (c3 != null) {
							mapint.put(c3.getName(), getValuesOfChoiceList(c3));
							objects.add(mapint);
						}
					} else {
						objects.add(c2.getValue().toString());
					}
				}
				map.put(c1.getName(), objects);
			}
		}
		return map;
	}

	public List getValuesOfChoiceListByProperty(String className, String propertyName)
			throws PropertyNotFoundException {
		Conect();
		List values = new ArrayList();

		ClassDescriptions coClasses = (ClassDescriptions) ObjectFactory.getObjectStore(this.store, getSession())
				.getClassDescriptions().filterByProperty("Id", 1, className);
		PropertyDescriptions propDescs = ObjectFactory.getObjectStore(this.store, getSession())
				.getPropertyDescriptions(coClasses);

		PropertyDescription desc = null;
		for (int i = 0; i < propDescs.size(); i++) {
			String name = ((PropertyDescription) propDescs.get(i)).getPropertyStringValue("SymbolicName");
			if (name.equalsIgnoreCase(propertyName)) {
				desc = (PropertyDescription) propDescs.get(i);
				break;
			}
		}
		if ((desc != null) && (desc.getChoices(1) != null)) {
			ChoiceList c1 = desc.getChoices(1);
			for (int j = 0; j < c1.size(); j++) {
				Choice c2 = (Choice) c1.get(j);
				values.add(c2.getValue().toString());
			}
		}
		return values;
	}

	public HashMap getSubClassesOf(String idOrClassName) throws Exception {
		Conect();
		HashMap objects = new HashMap();
		if ((idOrClassName != null) && (idOrClassName.length() > 0) && (getClassType(idOrClassName) > 0)) {
			ClassDescription classDesc = (ClassDescription) ObjectFactory.getObjectStore(this.store, getSession())
					.getObject(25, idOrClassName);
			ClassDescriptions c = ObjectFactory.getClassDescriptions();
			c.add(classDesc);
			ClassDescriptions classDescs = ObjectFactory.getObjectStore(this.store, getSession())
					.getClassDescriptions(c, false);
			for (int i = 0; i < classDescs.size(); i++) {
				ClassDescription cd = (ClassDescription) classDescs.get(i);
				if (cd.isInstantiableByUser()) {
					objects.put(cd.getName(), cd.getId());
				}
			}
		}
		return objects;
	}

	public HashMap getPropertyDefinitionsByClass(String idOrClassName) {
		Conect();
		HashMap objects = new HashMap();
		if ((idOrClassName != null) && (idOrClassName.length() > 0)) {
			ClassDescription classDesc = (ClassDescription) ObjectFactory.getObjectStore(this.store, getSession())
					.getObject(25, idOrClassName);
			PropertyDescriptions pdesc = classDesc.getPropertyDescriptions(false);
			for (int i = 0; i < pdesc.size(); i++) {
				PropertyDescription propDesc = (PropertyDescription) pdesc.get(i);
				try {
					if (!propDesc.getPropertyBooleanValue("IsSystemGenerated")) {
						HashMap props = new HashMap();
						props.put("IsValueRequired", propDesc.getPropertyStringValue("IsValueRequired"));
						props.put("SymbolicName", propDesc.getPropertyStringValue("SymbolicName"));
						props.put("DataType", new Integer(propDesc.getPropertyIntValue("DataType")));
						props.put("Cardinality", new Integer(propDesc.getPropertyIntValue("Cardinality")));
						props.put("Settability", new Integer(propDesc.getPropertyIntValue("Settability")));
						if (propDesc.getPropertyIntValue("DataType") == 8) {
							props.put("MaximumLengthString",
									new Integer(propDesc.getPropertyIntValue("MaximumLengthString")));
						}
						objects.put(propDesc.getName(), props);
					}
				} catch (PropertyNotFoundException e) {
					if (this.log.isDebugEnabled()) {
						this.log.debug(e.getMessage());
					}
				}
			}
		}
		return objects;
	}

	public List getLinksByDocument(Object idDocumentOrDocument) {
		Conect();
		Document document = null;
		List list = null;
		if ((idDocumentOrDocument instanceof String)) {
			document = (Document) ObjectFactory.getObjectStore(this.store, getSession()).getObject(1,
					(String) idDocumentOrDocument);
		} else if ((idDocumentOrDocument instanceof Document)) {
			document = (Document) idDocumentOrDocument;
		} else {
			return list;
		}
		list = new ArrayList();
		Links links = document.getTailLinks(null);
		for (int i = 0; i < links.size(); i++) {
			try {
				Link link = (Link) links.get(i);
				list.add((Document) link.getPropertyValue("Head"));
			} catch (Exception ex) {
				if (this.log.isDebugEnabled()) {
					this.log.debug(ex.getMessage());
				}
			}
		}
		return list;
	}

	public String getConfigFile() {
		return this.configFile;
	}

	public void setConfigFile(String pathConfig) {
		this.configFile = pathConfig;
	}

	public Session getSession() {
		return this.session;
	}

	public static Object getObjectByValue(Object value) {
		Object obj = null;
		try {
			return formatDate.parse(value.toString());
		} catch (Exception localException) {
			try {
				return new Integer(Integer.parseInt(value.toString()));
			} catch (Exception localException1) {
				try {
					if ((!value.toString().equalsIgnoreCase("true")) && (!value.toString().equalsIgnoreCase("false"))) {
						return null;
					}
					return Boolean.valueOf(value.toString());
				} catch (Exception localException2) {
				}
				try {
					label74: return value.toString();
				} catch (Exception localException3) {
				}
			}
		}
		return obj;
	}

	public boolean isUniquenessConstraint() {
		return this.uniquenessConstraint;
	}

	public void setUniquenessConstraint(boolean uniquenessConstraint) {
		this.uniquenessConstraint = uniquenessConstraint;
	}

	public String getMultivalueSplit() {
		return this.multivalueSplit;
	}

	public void setMultivalueSplit(String multivalueSplit) {
		this.multivalueSplit = multivalueSplit;
	}

	public static void setFormatDate(String strformatDate) {
		formatDate = new SimpleDateFormat(strformatDate);
	}

	public static SimpleDateFormat getFormatDate() {
		return formatDate;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public static String getTimeZone() {
		return timeZone;
	}

	public static void setTimeZone(String timeZone) {
		timeZone = timeZone;
	}

	public static String getPatternDate() {
		return patternDate;
	}

	public static void setPatternDate(String patternDate) {
		patternDate = patternDate;
	}

	public static void setFormatDate(SimpleDateFormat formatDate) {
		formatDate = formatDate;
	}
}