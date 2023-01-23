module axi4_addr_map #(
  parameter [33:0] C_MEM_START_ADDR = 34'h80000000,
  parameter C_ID_BITS = 4
) (

  (*
    X_INTERFACE_INFO = "xilinx.com:signal:clock:1.0 clk CLK", X_INTERFACE_PARAMETER = "ASSOCIATED_RESET aresetn ASSOCIATED_BUSIF S_AXI4:M_AXI4"
     *)
  input         clk,

  (* X_INTERFACE_INFO = "xilinx.com:signal:reset:1.0 reset RST", X_INTERFACE_PARAMETER = "POLARITY ACTIVE_LOW" *)
  input         aresetn,

  (*
    X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWREADY", X_INTERFACE_PARAMETER = "CLK_DOMAIN clk, PROTOCOL AXI4, ADDR_WIDTH 34, DATA_WIDTH 64"
     *)
  output        s_axi4_aw_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWVALID" *)
  input         s_axi4_aw_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWID" *)
  input  [C_ID_BITS-1:0]  s_axi4_aw_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWADDR" *)
  input  [33:0] s_axi4_aw_bits_addr,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWLEN" *)
  input  [7:0]  s_axi4_aw_bits_len,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWSIZE" *)
  input  [2:0]  s_axi4_aw_bits_size,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWBURST" *)
  input  [1:0]  s_axi4_aw_bits_burst,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWLOCK" *)
  input         s_axi4_aw_bits_lock,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWCACHE" *)
  input  [3:0]  s_axi4_aw_bits_cache,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWPROT" *)
  input  [2:0]  s_axi4_aw_bits_prot,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 AWQOS" *)
  input  [3:0]  s_axi4_aw_bits_qos,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 WREADY" *)
  output        s_axi4_w_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 WVALID" *)
  input         s_axi4_w_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 WDATA" *)
  input  [63:0] s_axi4_w_bits_data,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 WSTRB" *)
  input  [7:0]  s_axi4_w_bits_strb,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 WLAST" *)
  input         s_axi4_w_bits_last,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 BREADY" *)
  input         s_axi4_b_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 BVALID" *)
  output        s_axi4_b_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 BID" *)
  output [C_ID_BITS-1:0]  s_axi4_b_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 BRESP" *)
  output [1:0]  s_axi4_b_bits_resp,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARREADY" *)
  output        s_axi4_ar_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARVALID" *)
  input         s_axi4_ar_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARID" *)
  input  [C_ID_BITS-1:0]  s_axi4_ar_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARADDR" *)
  input  [33:0] s_axi4_ar_bits_addr,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARLEN" *)
  input  [7:0]  s_axi4_ar_bits_len,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARSIZE" *)
  input  [2:0]  s_axi4_ar_bits_size,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARBURST" *)
  input  [1:0]  s_axi4_ar_bits_burst,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARLOCK" *)
  input         s_axi4_ar_bits_lock,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARCACHE" *)
  input  [3:0]  s_axi4_ar_bits_cache,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARPROT" *)
  input  [2:0]  s_axi4_ar_bits_prot,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 ARQOS" *)
  input  [3:0]  s_axi4_ar_bits_qos,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RREADY" *)
  input         s_axi4_r_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RVALID" *)
  output        s_axi4_r_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RID" *)
  output [C_ID_BITS-1:0]  s_axi4_r_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RDATA" *)
  output [63:0] s_axi4_r_bits_data,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RRESP" *)
  output [1:0]  s_axi4_r_bits_resp,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 S_AXI4 RLAST" *)
  output        s_axi4_r_bits_last,

  (*
    X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWREADY", X_INTERFACE_PARAMETER = "CLK_DOMAIN clk, PROTOCOL AXI4, ADDR_WIDTH 34, DATA_WIDTH 64"
     *)
  input         m_axi4_aw_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWVALID" *)
  output        m_axi4_aw_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWID" *)
  output [C_ID_BITS-1:0]  m_axi4_aw_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWADDR" *)
  output [33:0] m_axi4_aw_bits_addr,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWLEN" *)
  output [7:0]  m_axi4_aw_bits_len,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWSIZE" *)
  output [2:0]  m_axi4_aw_bits_size,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWBURST" *)
  output [1:0]  m_axi4_aw_bits_burst,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWLOCK" *)
  output        m_axi4_aw_bits_lock,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWCACHE" *)
  output [3:0]  m_axi4_aw_bits_cache,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWPROT" *)
  output [2:0]  m_axi4_aw_bits_prot,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 AWQOS" *)
  output [3:0]  m_axi4_aw_bits_qos,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 WREADY" *)
  input         m_axi4_w_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 WVALID" *)
  output        m_axi4_w_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 WDATA" *)
  output [63:0] m_axi4_w_bits_data,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 WSTRB" *)
  output [7:0]  m_axi4_w_bits_strb,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 WLAST" *)
  output        m_axi4_w_bits_last,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 BREADY" *)
  output        m_axi4_b_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 BVALID" *)
  input         m_axi4_b_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 BID" *)
  input  [C_ID_BITS-1:0]  m_axi4_b_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 BRESP" *)
  input  [1:0]  m_axi4_b_bits_resp,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARREADY" *)
  input         m_axi4_ar_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARVALID" *)
  output        m_axi4_ar_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARID" *)
  output [C_ID_BITS-1:0]  m_axi4_ar_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARADDR" *)
  output [33:0] m_axi4_ar_bits_addr,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARLEN" *)
  output [7:0]  m_axi4_ar_bits_len,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARSIZE" *)
  output [2:0]  m_axi4_ar_bits_size,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARBURST" *)
  output [1:0]  m_axi4_ar_bits_burst,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARLOCK" *)
  output        m_axi4_ar_bits_lock,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARCACHE" *)
  output [3:0]  m_axi4_ar_bits_cache,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARPROT" *)
  output [2:0]  m_axi4_ar_bits_prot,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 ARQOS" *)
  output [3:0]  m_axi4_ar_bits_qos,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RREADY" *)
  output        m_axi4_r_ready,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RVALID" *)
  input         m_axi4_r_valid,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RID" *)
  input  [C_ID_BITS-1:0]  m_axi4_r_bits_id,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RDATA" *)
  input  [63:0] m_axi4_r_bits_data,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RRESP" *)
  input  [1:0]  m_axi4_r_bits_resp,

  (* X_INTERFACE_INFO = "xilinx.com:interface:aximm:1.0 M_AXI4 RLAST" *)
  input         m_axi4_r_bits_last
);

assign s_axi4_aw_ready 		= m_axi4_aw_ready;
assign m_axi4_aw_valid 		= s_axi4_aw_valid;
assign m_axi4_aw_bits_id	= s_axi4_aw_bits_id;
assign m_axi4_aw_bits_addr	= s_axi4_aw_bits_addr - C_MEM_START_ADDR;
assign m_axi4_aw_bits_len	= s_axi4_aw_bits_len;
assign m_axi4_aw_bits_size	= s_axi4_aw_bits_size;
assign m_axi4_aw_bits_burst	= s_axi4_aw_bits_burst;
assign m_axi4_aw_bits_lock	= s_axi4_aw_bits_lock;
assign m_axi4_aw_bits_cache	= s_axi4_aw_bits_cache;
assign m_axi4_aw_bits_prot	= s_axi4_aw_bits_prot;
assign m_axi4_aw_bits_qos	= s_axi4_aw_bits_qos;
assign s_axi4_w_ready		= m_axi4_w_ready;
assign m_axi4_w_valid		= s_axi4_w_valid;
assign m_axi4_w_bits_data	= s_axi4_w_bits_data;
assign m_axi4_w_bits_strb	= s_axi4_w_bits_strb;
assign m_axi4_w_bits_last	= s_axi4_w_bits_last;
assign m_axi4_b_ready		= s_axi4_b_ready;
assign s_axi4_b_valid		= m_axi4_b_valid;
assign s_axi4_b_bits_id		= m_axi4_b_bits_id;
assign s_axi4_b_bits_resp	= m_axi4_b_bits_resp;
assign s_axi4_ar_ready		= m_axi4_ar_ready;
assign m_axi4_ar_valid		= s_axi4_ar_valid;
assign m_axi4_ar_bits_id	= s_axi4_ar_bits_id;
assign m_axi4_ar_bits_addr	= s_axi4_ar_bits_addr - C_MEM_START_ADDR;
assign m_axi4_ar_bits_len	= s_axi4_ar_bits_len;
assign m_axi4_ar_bits_size	= s_axi4_ar_bits_size;
assign m_axi4_ar_bits_burst	= s_axi4_ar_bits_burst;
assign m_axi4_ar_bits_lock	= s_axi4_ar_bits_lock;
assign m_axi4_ar_bits_cache	= s_axi4_ar_bits_cache;
assign m_axi4_ar_bits_prot	= s_axi4_ar_bits_prot;
assign m_axi4_ar_bits_qos	= s_axi4_ar_bits_qos;
assign m_axi4_r_ready		= s_axi4_r_ready;
assign s_axi4_r_valid		= m_axi4_r_valid;
assign s_axi4_r_bits_id		= m_axi4_r_bits_id;
assign s_axi4_r_bits_data	= m_axi4_r_bits_data;
assign s_axi4_r_bits_resp	= m_axi4_r_bits_resp;
assign s_axi4_r_bits_last	= m_axi4_r_bits_last;

endmodule
