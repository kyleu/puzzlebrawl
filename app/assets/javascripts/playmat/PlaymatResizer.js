/* global define:false */
/* global Phaser:false */
/* global _:false */
define([], function () {
  'use strict';

  var PlaymatResizer = function(playmat) {
    this.playmat = playmat;
  };

  PlaymatResizer.prototype.refreshLayout = function() {
    var p = this.playmat;
    var selfId = this.playmat.self;
    if(selfId === null) {
      throw 'No self id.';
    }

    var originalSize = [p.w, p.h];

    var splitPlayers = _.partition(p.players, function(player) { return player.id === selfId; });

    if(splitPlayers.length !== 2 || splitPlayers[0].length !== 1) {
      throw 'Incomplete board definitions';
    }

    var self = splitPlayers[0][0];
    var others = splitPlayers[1];

    var xOffset = 32;

    self.nameLabel.x = xOffset;
    self.nameLabel.y = 32;

    self.scoreLabel.x = xOffset + self.board.width;
    self.scoreLabel.y = 32;

    self.board.x = xOffset;
    self.board.y = 112;

    xOffset += self.board.width + 32;

    _.each(others, function(player) {
      player.nameLabel.x = xOffset;
      player.nameLabel.y = 32;

      player.scoreLabel.x = xOffset + player.board.width;
      player.scoreLabel.y = 32;

      player.board.x = xOffset;
      player.board.y = 112;

      xOffset += player.board.width + 32;
    });

    p.w = xOffset;
    p.h = (12 * 256) + 144;

    if(p.w !== originalSize[0] || p.h !== originalSize[1]) {
      this.resize();
    }
  };

  PlaymatResizer.prototype.resize = function() {
    var p = this.playmat;
    var totalHeight = p.game.world.height;

    var widthRatio = p.game.world.width / p.w;
    var heightRatio = totalHeight / p.h;

    var newPosition = p.position;
    var newScale = p.scale;

    if(widthRatio < heightRatio) {
      newScale = new Phaser.Point(widthRatio, widthRatio);
      var yOffset = (totalHeight - (p.h * widthRatio)) / 2;
      if(yOffset > 0 || p.y !== 0) {
        newPosition = new Phaser.Point(0, yOffset);
      }
    } else {
      newScale = new Phaser.Point(heightRatio, heightRatio);
      var xOffset = (p.game.world.width - (p.w * heightRatio)) / 2;
      if(xOffset > 0 || p.x !== 0) {
        newPosition = new Phaser.Point(xOffset, 0);
      }
    }

    if(p.initialized) {
      p.game.add.tween(p.scale).to({x: newScale.x, y: newScale.y}, 500, Phaser.Easing.Quadratic.InOut, true);
      p.game.add.tween(p.position).to({x: newPosition.x, y: newPosition.y}, 500, Phaser.Easing.Quadratic.InOut, true);
    } else {
      p.scale = newScale;
      p.position = newPosition;
      p.initialized = true;
    }
  };

  return PlaymatResizer;
});
