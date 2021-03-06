/*
 * Copyright (C) 2003-2018 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.onlyoffice;

import org.exoplatform.onlyoffice.webui.OnlyofficeEditorUIService;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * Onlyoffice editor config for its JS API. <br>
 * This class implements {@link Externalizable} for serialization in eXo cache (actual in cluster).
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: Editor.java 00000 Feb 12, 2016 pnedonosko $
 */
public class Config implements Externalizable {

  /** The Constant DATETIME_FORMAT. */
  protected static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

  /** The Constant NO_LANG. */
  protected static final String           NO_LANG         = "no_lang".intern();

  /** The Constant EMPTY. */
  protected static final String           EMPTY           = "".intern();

  /**
   * The Class Builder.
   */
  public static class Builder {

    /** The workspace. */
    protected final String workspace;

    /** The path. */
    protected final String path;

    /** The document type. */
    protected final String documentType;

    /** The documentserver url. */
    // DocumentServer link
    protected final String documentserverUrl;

    /** The platform url. */
    // if set will be used to generate file and callback URLs, for this config and its copies for other users.
    protected String       platformUrl;

    /** The url. */
    // Document
    protected String       fileType, key, title, url;

    /** The folder. */
    // Document.Info
    protected String       author, created, folder;

    /** The mode. */
    // Editor
    protected String       callbackUrl, lang, mode;

    /** The lastname. */
    // Editor.User
    protected String       userId, firstname, lastname;

    /**
     * Instantiates a new builder.
     *
     * @param documentserverUrl the documentserver url
     * @param documentType the document type
     * @param workspace the workspace
     * @param path the path
     */
    protected Builder(String documentserverUrl, String documentType, String workspace, String path) {
      this.documentserverUrl = documentserverUrl;
      this.documentType = documentType;
      this.workspace = workspace;
      this.path = path;
    }

    /**
     * Generate file and callback URLs using given Platform base URL. This will erase these URLs explicitly
     * set previously.
     *
     * @param platformUrl the platform url
     * @return the builder
     */
    public Builder generateUrls(String platformUrl) {
      this.platformUrl = platformUrl;
      return this;
    }

    // Document: fileType, key, title, url

    /**
     * Title.
     *
     * @param title the title
     * @return the builder
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Key.
     *
     * @param key the key
     * @return the builder
     */
    public Builder key(String key) {
      this.key = key;
      return this;
    }

    /**
     * File type.
     *
     * @param fileType the file type
     * @return the builder
     */
    public Builder fileType(String fileType) {
      this.fileType = fileType;
      return this;
    }

    /**
     * Url.
     *
     * @param url the url
     * @return the builder
     */
    public Builder url(String url) {
      this.url = url;
      return this;
    }

    // Document.info: author, created, folder

    /**
     * Author.
     *
     * @param author the author
     * @return the builder
     */
    public Builder author(String author) {
      this.author = author;
      return this;
    }

    /**
     * Created.
     *
     * @param createdTime the created time
     * @return the builder
     */
    public Builder created(Calendar createdTime) {
      this.created = DATETIME_FORMAT.format(createdTime.getTime());
      return this;
    }

    /**
     * Folder.
     *
     * @param folder the folder
     * @return the builder
     */
    public Builder folder(String folder) {
      this.folder = folder;
      return this;
    }

    /**
     * Callback url.
     *
     * @param callbackUrl the callback url
     * @return the builder
     */
    // Editor: callbackUrl, lang, mode
    public Builder callbackUrl(String callbackUrl) {
      this.callbackUrl = callbackUrl;
      return this;
    }

    /**
     * Lang.
     *
     * @param lang the lang
     * @return the builder
     */
    public Builder lang(String lang) {
      this.lang = lang;
      return this;
    }

    /**
     * Mode.
     *
     * @param mode the mode
     * @return the builder
     */
    public Builder mode(String mode) {
      this.mode = mode;
      return this;
    }

    // Editor.User: userId, firstname, lastname

    /**
     * User id.
     *
     * @param userId the user id
     * @return the builder
     */
    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * User first name.
     *
     * @param firstname the firstname
     * @return the builder
     */
    public Builder userFirstName(String firstname) {
      this.firstname = firstname;
      return this;
    }

    /**
     * User last name.
     *
     * @param lastname the lastname
     * @return the builder
     */
    public Builder userLastName(String lastname) {
      this.lastname = lastname;
      return this;
    }

    /**
     * Builds the.
     *
     * @return the config
     */
    public Config build() {
      if (platformUrl != null) {
        this.url = new StringBuilder(platformUrl).append("/content/").append(userId).append("/").append(key).toString();
        this.callbackUrl = new StringBuilder(platformUrl).append("/status/").append(userId).append("/").append(key).toString();
      }

      Document.Info info = new Document.Info(author, created, folder);
      Document.Permissions permissions = new Document.EditPermissions();
      Document document = new Document(key, fileType, title, url, info, permissions);
      Editor.User user = new Editor.User(userId, firstname, lastname);
      Editor editor = new Editor(callbackUrl, lang, mode, user);
      return new Config(documentserverUrl, platformUrl, workspace, path, documentType, document, editor);
    }
  }

  /**
   * The Class Document.
   */
  public static class Document {

    /**
     * The Class Info.
     */
    public static class Info {

      /** The author. */
      protected final String author;

      /** The created. */
      protected final String created; // '2010-07-07 3:46 PM'

      /** The folder. */
      protected final String folder;  // 'Example Files'

      // TODO there is also sharingSettings array where we can put users with different access/edit
      // permissions: 'Full Access', 'Read Only'

      /**
       * Instantiates a new info.
       *
       * @param author the author
       * @param created the created
       * @param folder the folder
       */
      protected Info(String author, String created, String folder) {
        super();
        this.author = author;
        this.created = created;
        this.folder = folder;
      }

      /**
       * Gets the author.
       *
       * @return the author
       */
      public String getAuthor() {
        return author;
      }

      /**
       * Gets the created.
       *
       * @return the created
       */
      public String getCreated() {
        return created;
      }

      /**
       * Gets the folder.
       *
       * @return the folder
       */
      public String getFolder() {
        return folder;
      }

    }

    /**
     * The Class Permissions.
     */
    public static abstract class Permissions {

      /** The download. */
      protected final boolean download;

      /** The edit. */
      protected final boolean edit;

      /**
       * Instantiates a new permissions.
       *
       * @param download the download
       * @param edit the edit
       */
      protected Permissions(boolean download, boolean edit) {
        this.download = download;
        this.edit = edit;
      }

      /**
       * Checks if is download.
       *
       * @return the download
       */
      public boolean isDownload() {
        return download;
      }

      /**
       * Checks if is edits the.
       *
       * @return the edit
       */
      public boolean isEdit() {
        return edit;
      }

    }

    /**
     * The Class EditPermissions.
     */
    public static class EditPermissions extends Permissions {

      /**
       * Instantiates a new edits the permissions.
       */
      protected EditPermissions() {
        super(true, true);
      }
    }

    /** The file type. */
    protected final String      fileType;

    /** The key. */
    protected final String      key;

    /** The title. */
    protected final String      title;

    /** The url. */
    protected final String      url;

    /** The info. */
    protected final Info        info;

    /** The permissions. */
    protected final Permissions permissions;

    /**
     * Instantiates a new document.
     *
     * @param key the key
     * @param fileType the file type
     * @param title the title
     * @param url the url
     * @param info the info
     * @param permissions the permissions
     */
    protected Document(String key, String fileType, String title, String url, Info info, Permissions permissions) {
      super();
      this.fileType = fileType;
      this.key = key;
      this.title = title;
      this.url = url;
      this.info = info;
      this.permissions = permissions;
    }

    /**
     * For user.
     *
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param url the url
     * @return the document
     */
    protected Document forUser(String id, String firstName, String lastName, String url) {
      return new Document(key, fileType, title, url, info, permissions);
    }

    /**
     * Gets the file type.
     *
     * @return the fileType
     */
    public String getFileType() {
      return fileType;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
      return key;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
      return title;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
      return url;
    }

    /**
     * Gets the info.
     *
     * @return the info
     */
    public Info getInfo() {
      return info;
    }

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    public Permissions getPermissions() {
      return permissions;
    }

  }

  /**
   * The Class Editor.
   */
  public static class Editor {

    /**
     * The Class User.
     */
    public static class User {

      /** The id. */
      protected final String     id;

      /** The firstname. */
      protected final String     firstname;

      /** The lastname. */
      protected final String     lastname;

      /** The username. */
      protected final String     username;

      /** The lock token. */
      protected transient String lockToken;

      /**
       * Instantiates a new user.
       *
       * @param id the id
       * @param firstname the firstname
       * @param lastname the lastname
       */
      protected User(String id, String firstname, String lastname) {
        super();
        this.id = id;
        this.username = id;
        this.firstname = firstname;
        this.lastname = lastname;
      }

      /**
       * Gets the id.
       *
       * @return the id
       */
      public String getId() {
        return id;
      }

      /**
       * Gets the username.
       *
       * @return the username
       */
      public String getUsername() {
        return username;
      }

      /**
       * Gets the firstname.
       *
       * @return the firstname
       */
      public String getFirstname() {
        return firstname;
      }

      /**
       * Gets the lastname.
       *
       * @return the lastname
       */
      public String getLastname() {
        return lastname;
      }

      /**
       * Gets the lock token.
       *
       * @return the lockToken
       */
      protected String getLockToken() {
        return lockToken;
      }

      /**
       * Sets the lock token.
       *
       * @param lockToken the lockToken to set
       */
      protected void setLockToken(String lockToken) {
        this.lockToken = lockToken;
      }

    }

    /** The callback url. */
    protected final String callbackUrl;

    /** The mode. */
    protected final String mode;

    /** The user. */
    protected final User   user;

    /** The lang. */
    protected String       lang;

    /**
     * Instantiates a new editor.
     *
     * @param callbackUrl the callback url
     * @param lang the lang
     * @param mode the mode
     * @param user the user
     */
    protected Editor(String callbackUrl, String lang, String mode, User user) {
      super();
      this.callbackUrl = callbackUrl;
      this.lang = lang;
      this.mode = mode;
      this.user = user;
    }

    /**
     * Gets the callback url.
     *
     * @return the callbackUrl
     */
    public String getCallbackUrl() {
      return callbackUrl;
    }

    /**
     * Gets the language of user editor.
     *
     * @return the lang can be <code>null</code> if unable to define from user profile
     */
    public String getLang() {
      return lang;
    }

    /**
     * Sets the lang.
     *
     * @param lang the lang to set
     */
    public void setLang(String lang) {
      this.lang = lang;
    }

    /**
     * Gets the mode.
     *
     * @return the mode
     */
    public String getMode() {
      return mode;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
      return user;
    }

    /**
     * For user.
     *
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param lang the lang
     * @param callbackUrl the callback url
     * @return the editor
     */
    protected Editor forUser(String id, String firstName, String lastName, String lang, String callbackUrl) {
      User otherUser = new User(id, firstName, lastName);
      // FYI locks maintenance will introduce complex logic
      // simpler: each user may contain own lock token only, but don't rely on others
      // otherUser.setLockToken(user.getLockToken());
      return new Editor(callbackUrl, lang, mode, otherUser);
    }
  }

  /**
   * Editor.
   *
   * @param documentserverUrl the documentserver url
   * @param workspace the workspace
   * @param path the path
   * @param documentType the document type
   * @return the builder
   */
  protected static Builder editor(String documentserverUrl, String workspace, String path, String documentType) {
    return new Builder(documentserverUrl, documentType, workspace, path);
  }

  /**
   * File url.
   *
   * @param platformUrl the platform url
   * @param userId the user id
   * @param key the key
   * @return the string
   */
  protected static String fileUrl(String platformUrl, String userId, String key) {
    return new StringBuilder(platformUrl).append("/content/").append(userId).append("/").append(key).toString();
  }

  /**
   * Callback url.
   *
   * @param platformUrl the platform url
   * @param userId the user id
   * @param key the key
   * @return the string
   */
  protected static String callbackUrl(String platformUrl, String userId, String key) {
    return new StringBuilder(platformUrl).append("/status/").append(userId).append("/").append(key).toString();
  }

  /** The documentserver js url. */
  private String         documentserverUrl, documentserverJsUrl;

  /** The platform url. */
  private String         platformUrl;

  /** The workspace. */
  private String         workspace;

  /** The path. */
  private String         path;

  /** The document type. */
  private String         documentType;

  /** The document. */
  private Document       document;

  /** The editor config. */
  private Editor         editorConfig;

  /** The error. */
  private String         error;

  /** The node. */
  private transient Node node;

  /**
   * Marker of editor state. By default editor state is undefined and will be treated as not open nor not
   * closed. When editor will be open in Onlyoffice it will send a status (1) and then need mark the editor
   * open.
   */
  private Boolean        open;

  /**
   * Marker for transient state between an UI closed in eXo and actually saved data submitted from Onlyoffice
   * DS. This state managed by {@link OnlyofficeEditorUIService} and set here for client information only.
   */
  private Boolean        closing;

  /**
   * Instantiates a new config for use with {@link Externalizable} methods.
   */
  public Config() {
    // nothing
  }

  /**
   * Editor config constructor.
   *
   * @param documentserverUrl the documentserver url
   * @param platformUrl the platform url
   * @param workspace the workspace
   * @param path the path
   * @param documentType the document type
   * @param document the document
   * @param editor the editor
   */
  protected Config(String documentserverUrl,
                   String platformUrl,
                   String workspace,
                   String path,
                   String documentType,
                   Document document,
                   Editor editor) {
    this.workspace = workspace;
    this.path = path;
    this.documentType = documentType;
    this.documentserverUrl = documentserverUrl;
    this.documentserverJsUrl = new StringBuilder(documentserverUrl).append("apps/api/documents/api.js").toString();

    this.platformUrl = platformUrl;

    this.document = document;
    this.editorConfig = editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    // Strings
    out.writeUTF(workspace);
    out.writeUTF(path);
    out.writeUTF(documentType);
    out.writeUTF(documentserverUrl);
    out.writeUTF(documentserverJsUrl);
    out.writeUTF(platformUrl);
    out.writeUTF(open != null ? open.toString() : EMPTY);
    // Note: closing state isn't replicable
    out.writeUTF(error != null ? error : EMPTY);

    // Objects
    // Document: key, fileType, title, url, info(author, created, folder)
    out.writeUTF(document.getKey());
    out.writeUTF(document.getFileType());
    out.writeUTF(document.getTitle());
    out.writeUTF(document.getUrl());
    out.writeUTF(document.getInfo().getAuthor());
    out.writeUTF(document.getInfo().getCreated());
    out.writeUTF(document.getInfo().getFolder());

    // Editor: callbackUrl, lang, mode, user(userId, firstname, lastname)
    out.writeUTF(editorConfig.getCallbackUrl());
    String elang = editorConfig.getLang();
    out.writeUTF(elang != null ? elang : NO_LANG);
    out.writeUTF(editorConfig.getMode());
    out.writeUTF(editorConfig.getUser().getId());
    out.writeUTF(editorConfig.getUser().getFirstname());
    out.writeUTF(editorConfig.getUser().getLastname());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    // Strings
    this.workspace = in.readUTF();
    this.path = in.readUTF();
    this.documentType = in.readUTF();
    this.documentserverUrl = in.readUTF();
    this.documentserverJsUrl = in.readUTF();
    this.platformUrl = in.readUTF();
    String openString = in.readUTF();
    // Note: closing state isn't replicable (due to short lifecycle, few seconds max and it's valuable
    // per-user session only, but in cluster with sticky sessions an user will not call another server).
    if (EMPTY.equals(openString)) {
      open = closing = null;
    } else {
      open = Boolean.valueOf(openString);
      closing = new Boolean(false);
    }
    String errorString = in.readUTF();
    if (EMPTY.equals(errorString)) {
      error = null;
    } else {
      error = errorString;
    }

    // Objects
    // Document: key, fileType, title, url, info(author, created, folder)
    String dkey = in.readUTF();
    String dfileType = in.readUTF();
    String dtitle = in.readUTF();
    String durl = in.readUTF();
    String diauthor = in.readUTF();
    String dicreated = in.readUTF();
    String difolder = in.readUTF();
    Document.Info dinfo = new Document.Info(diauthor, dicreated, difolder);
    this.document = new Document(dkey, dfileType, dtitle, durl, dinfo, new Document.EditPermissions());

    // Editor: callbackUrl, lang, mode, user(userId, firstname, lastname)
    String ecallbackUrl = in.readUTF();
    String elang = in.readUTF();
    if (NO_LANG.equals(elang)) {
      elang = null;
    }
    String emode = in.readUTF();
    String euid = in.readUTF();
    String eufirstname = in.readUTF();
    String eulastname = in.readUTF();
    Editor.User euser = new Editor.User(euid, eufirstname, eulastname);
    this.editorConfig = new Editor(ecallbackUrl, elang, emode, euser);
  }

  /**
   * Gets the documentserver js url.
   *
   * @return the documentserverJsUrl
   */
  public String getDocumentserverJsUrl() {
    return documentserverJsUrl;
  }

  /**
   * Gets the documentserver url.
   *
   * @return the documentserverUrl
   */
  public String getDocumentserverUrl() {
    return documentserverUrl;
  }

  /**
   * Gets the context node.
   *
   * @return the node in context, can be <code>null</code>
   */
  public Node getContextNode() {
    return node;
  }

  /**
   * Sets the context node.
   *
   * @param node the node to set
   */
  protected void setContextNode(Node node) {
    this.node = node;
  }

  /**
   * Gets the workspace.
   *
   * @return the workspace
   */
  public String getWorkspace() {
    return workspace;
  }

  /**
   * Gets the path.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * Gets the document type.
   *
   * @return the documentType
   */
  public String getDocumentType() {
    return documentType;
  }

  /**
   * Gets the document.
   *
   * @return the config
   */
  public Document getDocument() {
    return document;
  }

  /**
   * Gets the editor config.
   *
   * @return the editor
   */
  public Editor getEditorConfig() {
    return editorConfig;
  }

  /**
   * Create a copy of this editor but for another given user.
   * 
   * @param id {@link String}
   * @param firstName {@link String}
   * @param lastName {@link String}
   * @param lang {@link String}
   * @return {@link Config} an instance of config similar to this but with another user in the editor
   */
  public Config forUser(String id, String firstName, String lastName, String lang) {
    return new Config(documentserverUrl,
                      platformUrl,
                      workspace,
                      path,
                      documentType,
                      document.forUser(id, firstName, lastName, fileUrl(platformUrl, id, document.getKey())),
                      editorConfig.forUser(id, firstName, lastName, lang, callbackUrl(platformUrl, id, document.getKey())));
  }

  /**
   * Checks if is created.
   *
   * @return true, if is created
   */
  public boolean isCreated() {
    return open == null;
  }

  /**
   * Checks if is editor open.
   *
   * @return true, if is open
   */
  public boolean isOpen() {
    return open != null ? open.booleanValue() : false;
  }

  /**
   * Checks if is editor closed (including closing state).
   *
   * @return true, if is in closed or closing state
   */
  public boolean isClosed() {
    return open != null ? !open.booleanValue() : false;
  }

  /**
   * Checks if is editor currently closing (saving the document). A closing state is a sub-form of closed
   * state.
   *
   * @return true of document in closing (saving) state
   */
  public boolean isClosing() {
    return closing != null ? closing.booleanValue() : false;
  }

  /**
   * Mark this config as open: user opened this editor.
   */
  public void open() {
    this.open = new Boolean(true);
    this.closing = new Boolean(false);
  }

  /**
   * Mark this config as closing: user already closed this editor but document not yet saved in the storage.
   * This state is actual for last user who will save the document submitted by the DS. Note that only already
   * open editor can be set to closing state, otherwise this method will have not effect.
   */
  public void closing() {
    if (open != null && open.booleanValue()) {
      this.open = new Boolean(false);
      this.closing = new Boolean(true);
    }
  }

  /**
   * Mark this config as closed: the editor closed, if it was last user in the editor, then its document
   * should be saved in the storage.
   */
  public void closed() {
    this.open = new Boolean(false);
    this.closing = new Boolean(false);
  }

  /**
   * Sets the error.
   *
   * @param error the new error
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * Checks for error.
   *
   * @return true, if successful
   */
  public boolean hasError() {
    return this.error != null;
  }

  /**
   * Gets the error.
   *
   * @return the error
   */
  public String getError() {
    return this.error;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Config) {
      Config other = (Config) obj;
      return this.documentType.equals(other.documentType) && this.workspace.equals(other.workspace) && this.path.equals(other.path);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(documentType);
    s.append(' ');
    s.append(workspace);
    s.append(':');
    s.append(path);
    if (open != null) {
      s.append(" (");
      s.append(open.booleanValue() ? "open" : (closing.booleanValue() ? "closing" : "closed"));
      s.append(')');
    }
    return s.toString();
  }

}
