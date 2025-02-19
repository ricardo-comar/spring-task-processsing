function OnUpdate(doc, meta, xattrs) {
    var request = {
        path: '/event',
        body: doc
    };
    
    var response = curl('POST', dest_server_binding, request);
    if (response.status == 200) {
      log("Successfully event sent ");
    }}

function OnDelete(meta, options) {
    log("Doc deleted/expired", meta.id);
}