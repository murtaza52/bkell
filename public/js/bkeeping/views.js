(function() {
  define([], function() {
    /*
      # Grab the Backbone object 
      */
    /*
      # Pure Template DIRECTIVES
      */
    var AccountRow, AccountView, AccountsView, EntriesView, EntryRow, EntryView, pureDirectives;
    pureDirectives = {
      accountsDirective: {
        "tbody tr": {
          "each<-puredata": {
            "a.editaccount@data-aid": "each.id",
            "td.name": "each.name",
            "td.type": "each.type",
            "td.weight": "each.counterWeight"
          }
        }
      },
      accountDirective: {
        "#account-name@value": "id"
      },
      entriesDirective: {
        "tbody tr": {
          "each<-puredata": {
            "a.editentry@href": function(arg) {
              return "/entries/entry/" + arg.each.item.id;
            },
            "td.date": "each.date",
            "td.name": "each.id",
            "td.balance": ""
          }
        }
      }
    };
    /*
      # Detail VIEWs
      */
    AccountView = Backbone.View.extend({
      initialize: function(options) {
        console.log('AccountView initialize CALLED');
        return this.el = $(options.el);
      },
      render: function(options) {
        console.log('AccountView render CALLED');
        $("#account-name").attr('value', this.model.get('name'));
        $("#account-type > option[value='" + (this.model.get('type')) + "']").attr('selected', 'selected');
        return $("#account-counterWeight > option[value='" + (this.model.get('counterWeight')) + "']").attr('selected', 'selected');
      }
    });
    EntryView = Backbone.View.extend();
    /*
      # Row VIEWs
      */
    AccountRow = Backbone.View.extend({
      initialize: function(options) {
        this.el = $(options.el);
        this.el.bind('change', this.accountChanged);
        this.el.find('.editaccount').bind('click', _.bind(this.editClicked, this));
        return this.el.find('.deleteaccount').bind('click', _.bind(this.deleteClicked, this));
      },
      editClicked: function() {
        return console.log('edit CLICKED');
      },
      deleteClicked: function() {
        return console.log('delete CLICKED');
      },
      accountChanged: function() {
        return console.log('account has been CHANGED');
      }
    });
    EntryRow = Backbone.View.extend({
      initialize: function(options) {
        this.el = $(options.el);
        this.el.bind('change', this.entryChanged);
        this.el.find('.editentry').bind('click', _.bind(this.editClicked, this));
        return this.el.find('.deleteentry').bind('click', _.bind(this.deleteClicked, this));
      },
      editClicked: function() {
        console.log('edit entry CLICKED');
        return false;
      },
      deleteClicked: function() {
        console.log('delete entry CLICKED');
        return false;
      },
      entryChanged: function() {
        console.log('entry has been CHANGED');
        return false;
      }
    });
    /*
      # Accounts and Entries VIEWs
      */
    AccountsView = Backbone.View.extend({
      el: $('#accounts'),
      initialize: function(options) {
        this.collection = options.collection;
        return this.collection.bind('reset', _.bind(this.render, this));
      },
      accountRows: [],
      render: function() {
        var ctx;
        console.log("AccountsView.render CALLED");
        ctx = this;
        return this.el.render({
          puredata: this.collection.toJSON()
        }, pureDirectives.accountsDirective).find('table').dataTable().find('tbody > tr').each(function(index, ech) {
          /*
                    # Nesting Row Views here
                    */
          var arow;
          arow = new AccountRow({
            el: ech
          });
          return ctx.accountRows.push(arow);
        });
      }
    });
    EntriesView = Backbone.View.extend({
      el: $('#right-col'),
      initialize: function(options) {
        this.collection = options.collection;
        return this.collection.bind('reset', _.bind(this.render, this));
      },
      entryRows: [],
      render: function() {
        var ctx;
        console.log("EntriesView.render CALLED");
        ctx = this;
        return this.el.render({
          puredata: this.collection.toJSON()
        }, pureDirectives.entriesDirective).find('table').dataTable().find('tbody > tr').each(function(index, ech) {
          /*
                    # Nesting Row Views here
                    */
          var arow;
          arow = new EntryRow({
            el: ech
          });
          return ctx.entryRows.push(arow);
        });
      }
    });
    return {
      /*
        # return an object with the View classes
        */
      AccountView: AccountView,
      EntryView: EntryView,
      AccountRow: AccountRow,
      EntryRow: EntryRow,
      AccountsView: AccountsView,
      EntriesView: EntriesView
    };
  });
}).call(this);
