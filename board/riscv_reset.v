module synchronizer(
  input wire clock,
  input wire dinp,
  output wire dout
);

(* SHREG_EXTRACT = "NO", ASYNC_REG = "TRUE" *)
reg [2:0] shreg;
always@(posedge clock) begin
  shreg <= {shreg[1:0], dinp};
end

assign dout = shreg[2];

endmodule

module riscv_reset(
  input wire clock,
  input wire sys_reset,
  input wire clock_ok,
  input wire mem_ok,
  input wire io_ok,
  (* X_INTERFACE_INFO = "xilinx.com:signal:reset:1.0 aresetn RST", X_INTERFACE_PARAMETER = "POLARITY ACTIVE_LOW" *)
  output wire aresetn,
  (* X_INTERFACE_INFO = "xilinx.com:signal:reset:1.0 reset RST", X_INTERFACE_PARAMETER = "POLARITY ACTIVE_HIGH" *)
  output wire reset
);

wire reset_inp, reset_sync;

assign reset_inp = sys_reset | ~clock_ok | ~mem_ok | ~io_ok;

synchronizer sync(
  .clock(clock),
  .dinp(reset_inp),
  .dout(reset_sync)
);

reg [4:0] reset_cnt = 5'b00000;
reg aresetn_reg, reset_reg;
always@(posedge clock) begin
  if (reset_sync) begin
    reset_cnt <= 5'b00000;
    aresetn_reg <= 1'b0;
    reset_reg <= 1'b1;
  end else if (reset_cnt < 5'b01111) begin
    reset_cnt <= reset_cnt + 1;
    aresetn_reg <= 1'b0;
    reset_reg <= 1'b1;
  end else if (reset_cnt < 5'b11111) begin
    reset_cnt <= reset_cnt + 1;
    aresetn_reg <= 1'b1;
    reset_reg <= 1'b1;
  end else begin
    aresetn_reg <= 1'b1;
    reset_reg <= 1'b0;
  end
end

assign aresetn = aresetn_reg;
assign reset = reset_reg;

endmodule
