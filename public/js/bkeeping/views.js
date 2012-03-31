(function() {
  define(['js/bkeeping/bkeeping'], function(bkeeping) {
    /*
      # Pure Template DIRECTIVES
      */
    var AccountRow, AccountView, AccountsView, EntriesView, EntryPartView, EntryRow, EntryView, pureDirectives;
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
            "a.editentry@data-eid": "each.id",
            "td.date": "each.date",
            "td.name": "each.id",
            "td.balance": ""
          }
        }
      },
      entryDirective: {
        "tbody tr": {
          "each<-puredata": {
            "a.editentrypart@data-eid": "each.id",
            "a.editentrypart@data-type": "each.tag",
            "td.debitAccount": function(arg) {
              return pureDirectives.determineAccountDtCt(this, "debit");
            },
            "td.debitAmount": function(arg) {
              return pureDirectives.determineAmountDtCt(this, "debit");
            },
            "td.creditAccount": function(arg) {
              return pureDirectives.determineAccountDtCt(this, "credit");
            },
            "td.creditAmount": function(arg) {
              return pureDirectives.determineAmountDtCt(this, "credit");
            }
          }
        }
      },
      determineCommon: function(arg, weight, attribute) {
        if (arg["tag"] === weight) {
          return arg[attribute];
        }
        return "&nbsp;";
      },
      determineAccountDtCt: function(arg, weight) {
        return pureDirectives.determineCommon(arg, weight, "accountid");
      },
      determineAmountDtCt: function(arg, weight) {
        return pureDirectives.determineCommon(arg, weight, "amount");
      },
      entryPartDirective: {
        "select#entry-part-account option": {
          "each<-puredata": {
            ".@value": "each.id",
            ".": "each.name"
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
    EntryView = Backbone.View.extend({
      initialize: function(options) {
        console.log('EntryView initialize CALLED');
        return this.el = $(options.el);
      },
      render: function(options) {
        var template;
        console.log('EntryView render CALLED');
        /*
              # clear the container each time - don't want to incrementally add
              */
        template = $("<tr> <td> <a class='editentrypart' href='#'>edit</a> </td> <td class='debitAccount'>Debit Account</td> <td class='debitAmount'>Debit Amount</td> <td>&nbsp;</td> <td class='creditAccount'>Credit Account</td> <td class='creditAmount'>Credit Amount</td> <td> <a class='deleteentrypart' href='#'>delete</a> </td> </tr>");
        $(".entry_container tbody").empty().append(template);
        return $(".entry_container").render({
          puredata: this.model.get('content')
        }, pureDirectives.entryDirective);
      }
    });
    EntryPartView = Backbone.View.extend({
      initialize: function(options) {
        console.log('EntryPartView initialize CALLED');
        return this.el = $(options.el);
      },
      render: function(options) {
        console.log('EntryPartView render CALLED');
        $(".entryPart_container .entryPart_content").render({
          puredata: this.accounts.toJSON()
        }, pureDirectives.entryPartDirective);
        $("#entry-part-amount").attr('value', this.model['amount']);
        $("#entry-part-account > option[value='" + this.model['accountid'] + "']").attr('selected', 'selected');
        return $("#entry-part-type > option[value='" + this.model['tag'] + "']").attr('selected', 'selected');
      }
    });
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
        this.collection.bind('reset', _.bind(this.render, this));
        this.collection.bind('add', _.bind(this.render, this));
        return this.collection.bind('change', _.bind(this.render, this));
      },
      accountRows: [],
      render: function() {
        var ctx, template;
        console.log("AccountsView.render CALLED");
        template = $("<table id='accounts-table'> <thead> <tr> <th></th> <th>Name</th> <th>Category</th> <th>Type</th> <th></th> </tr> </thead> <tbody> <tr> <td> <a class='editaccount' href='#'>edit</a> </td> <td class='name'>My Name</td> <td class='type'>My Type</td> <td class='weight'>My Weight</td> <td> <a class='deleteaccount' href='#'>delete</a> </td> </tr> </tbody> <tfoot> <tr> <td> <input id='account-add' type='button' value='Add' /> </td> <td>&nbsp;</td> <td>&nbsp;</td> <td>&nbsp;</td> <td>&nbsp;</td> </tr> </tfoot> </table>");
        $("#accounts-pane > .tab_container > .tab_content > .dataTables_wrapper").empty().append(template);
        ctx = this;
        return $("#accounts").render({
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
      },
      instrumentAccounts: function(elem, bindings, asm) {
        elem.find('.editaccount').unbind('click').bind('click', bindings, _.bind(asm.AsA, asm));
        return $("#account-add").unbind("click").bind("click", _.extend({
          account: new bkeeping.models.Account()
        }, bindings), _.bind(asm.AsA, asm));
      }
    });
    EntriesView = Backbone.View.extend({
      el: $('#entries'),
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
      EntryPartView: EntryPartView,
      AccountRow: AccountRow,
      EntryRow: EntryRow,
      AccountsView: AccountsView,
      EntriesView: EntriesView
    };
  });
}).call(this);
